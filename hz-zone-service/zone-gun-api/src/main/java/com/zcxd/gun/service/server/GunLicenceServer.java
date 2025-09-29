package com.zcxd.gun.service.server;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.Employee;
import com.zcxd.gun.db.model.GunLicence;
import com.zcxd.gun.feign.RemoteProvider;
import com.zcxd.gun.service.EmployeeService;
import com.zcxd.gun.service.GunLicenceService;
import com.zcxd.gun.service.GunSecurityService;
import com.zcxd.gun.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 保安证和持枪证
 * @author zccc
 */
@Service
public class GunLicenceServer {
    @Autowired
    GunSecurityService gunSecurityService;
    @Autowired
    GunLicenceService gunLicenceService;
    @Autowired
    EmployeeService employeeService;

    @Autowired
    RemoteProvider remoteService;

    private Result checkGunSecurity(GunSecurityVO gunSecurityVo) {
        // 是否有该员工
        Long employeeId = gunSecurityVo.getEmployeeId();
        if (employeeId != null) {
            Employee employee = employeeService.getById(employeeId);
            if (employee == null) {
                return Result.fail("未找到员工");
            }
        }

        if (gunSecurityVo.getDepartmentId() == null) {
            return Result.fail("部门设置错误");
        }

        // 之前是否已经有该员工的保安证信息
        if (gunSecurityService.gunSecurityNumExits(gunSecurityVo)) {
            return Result.fail("保安证号已经存在");
        }

        if (gunSecurityService.gunSecurityEmployeeExits(gunSecurityVo)) {
            return Result.fail("该人员已经有保安证");
        }

        return Result.success();
    }

    private Result checkGunLicence(GunLicenceVO gunLicenceVO) {
        // 是否有该员工
        Long employeeId = gunLicenceVO.getEmployeeId();
        if (employeeId != null) {
            Employee employee = employeeService.getById(employeeId);
            if (employee == null) {
                return Result.fail("未找到员工");
            }
        }

        if (gunLicenceVO.getDepartmentId() == null) {
            return Result.fail("部门设置错误");
        } else {
            Result<Boolean> authResult = remoteService.isAuthDepartment(gunLicenceVO.getDepartmentId());
            if (authResult.isFailed()) {
                return authResult;
            }
        }

        // 之前是否已经有该员工的保安证信息
        if (gunLicenceService.gunLicenceNumExits(gunLicenceVO)) {
            return Result.fail("持枪证号已经存在");
        }

        if (gunLicenceService.gunLicenceEmployeeExits(gunLicenceVO)) {
            return Result.fail("该人员已经有持枪证");
        }

        return Result.success();
    }

    /**
     * 新建保安证
     * @param gunSecurityVo 属性
     * @param file 图片
     * @return 结果
     */
    public Result saveGunSecurity(GunSecurityVO gunSecurityVo, MultipartFile file) {
        // 检查
        if (gunSecurityVo.getEmployeeId() == null) {
            return Result.fail("未设置员工");
        }

        Result checkRes = checkGunSecurity(gunSecurityVo);
        if (checkRes.isFailed()) {
            return checkRes;
        }

        // 上传文件
        Result result = gunSecurityService.uploadGunSecurityPhoto(gunSecurityVo, file);
        if (result.isFailed()) {
            return result;
        }

        return gunSecurityService.saveGunSecurity(gunSecurityVo);
    }

    /**
     * 新建持枪证
     * @param gunLicenceVO 属性
     * @param file 图片
     * @return 结果
     */
    public Result saveGunLicence(GunLicenceVO gunLicenceVO, MultipartFile file) {
        // 检查
        if (gunLicenceVO.getEmployeeId() == null) {
            return Result.fail("未设置员工");
        }
        if (StringUtils.isBlank(gunLicenceVO.getGunLicenceNum())) {
            return Result.fail("缺少持枪证号");
        }
        if (gunLicenceVO.getGunLicenceValidity() == null || gunLicenceVO.getGunLicenceValidity() <= 0) {
            return Result.fail("持枪证有效时间有误");
        }

        Result checkRes = checkGunLicence(gunLicenceVO);
        if (checkRes.isFailed()) {
            return checkRes;
        }

        // 上传文件
        Result result = gunLicenceService.uploadGunLicencePhoto(gunLicenceVO, file);
        if (result.isFailed()) {
            return result;
        }

        return gunLicenceService.saveGunLicence(gunLicenceVO);
    }

    /**
     * 修改保安证信息
     * @param gunSecurityVo 修改内容
     * @return 结果
     */
    public Object updateGunSecurityById(GunSecurityVO gunSecurityVo, MultipartFile file) {
        if (gunSecurityVo.getId() == null) {
            return Result.fail("未选中保安证");
        }

        Result checkGunSecurityRes = checkGunSecurity(gunSecurityVo);
        if (checkGunSecurityRes.isFailed()) {
            return checkGunSecurityRes;
        }

        // 上传文件
        Result result = gunSecurityService.uploadGunSecurityPhoto(gunSecurityVo, file);
        if (result.isFailed()) {
            return result;
        }

        return gunSecurityService.updateGunSecurityById(gunSecurityVo);
    }

    /**
     * 修改持枪证信息
     * @param gunLicenceVO 修改内容
     * @return 结果
     */
    public Object updateGunLicenceById(GunLicenceVO gunLicenceVO, MultipartFile file) {
        if (gunLicenceVO.getId() == null) {
            return Result.fail("未选中保安证");
        }

        Result checkGunSecurityRes = checkGunLicence(gunLicenceVO);
        if (checkGunSecurityRes.isFailed()) {
            return checkGunSecurityRes;
        }

        // 上传文件
        Result result = gunLicenceService.uploadGunLicencePhoto(gunLicenceVO, file);
        if (result.isFailed()) {
            return result;
        }

        return gunLicenceService.updateGunLicenceById(gunLicenceVO);
    }

    /**
     * 获取持枪证搜索wrapper
     * @param queryVo 搜索条件
     * @return wrapper
     */
    private MPJLambdaWrapper<GunLicence> getGunLicenceMPJLambdaWrapper(GunLicenceQueryVO queryVo) {
        MPJLambdaWrapper<GunLicence> wrapper = gunLicenceService.getGunLicenceMPJLambdaWrapper(queryVo);
        // 是否有图片
        if (queryVo.getHasPhoto() != null) {
            if (queryVo.getHasPhoto()) {
                wrapper.isNotNull(GunLicence::getGunLicencePhoto);
            } else {
                wrapper.isNull(GunLicence::getGunLicencePhoto);
            }
        }

        // 是否按照员工姓名查询
        if (!StringUtils.isBlank(queryVo.getName())) {
            Employee employee = new Employee();
            employee.setEmpName(queryVo.getName());
            employee.setDepartmentId(queryVo.getDepartmentId().longValue());
            List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);

            List<Long> ids = employeeByCondition.stream().map(Employee::getId).collect(Collectors.toList());
            // 为空不作为查询条件
            if (!ids.isEmpty()) {
                wrapper.in(GunLicence::getEmployeeId, ids);
            }
        }

        // 是否在有效期内
        if (queryVo.getIsValid() != null) {
            long time = System.currentTimeMillis();
            if (queryVo.getIsValid()) {
                wrapper.le(GunLicence::getGunLicenceValidity, time);
            } else {
                wrapper.gt(GunLicence::getGunLicenceValidity, time);
            }
        }
        return wrapper;
    }

    /**
     * 由于需要根据员工姓名查询，所以在该层实现
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     */
    public Object findListByPage(Integer page, Integer limit, GunLicenceQueryVO queryVo) throws IllegalAccessException {
        MPJLambdaWrapper<GunLicence> wrapper = getGunLicenceMPJLambdaWrapper(queryVo);
        Object listByPage = gunLicenceService.findListByPage(page, limit, wrapper);
        return listByPage;
    }

    /**
     * 导入持枪证
     * @param file 文件
     * @param departmentId 部门
     * @return 结果
     * @throws Exception 错误
     */
    @Transactional(rollbackFor = Exception.class)
    public Object gunLicenceExcelImport(MultipartFile file, Integer departmentId) throws Exception {
        if (file == null || file.getSize() == 0) {
            return Result.fail("文件不存在");
        }

        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return authResult;
        }

        GunLicenceExcelListener listener = new GunLicenceExcelListener(departmentId);
        // 读取所有Sheet的数据.每次读完一个Sheet就会调用这个方法
        EasyExcel.read(file.getInputStream(), GunLicenceImportVO.class, listener).sheet(0).doRead();
        List<GunLicenceImportVO> gunImportVos = listener.getDatas();
        List<String> failedInfo = listener.getFailedInfo();

        // 如果存在错误，需要重新修改
        if (!failedInfo.isEmpty()) {
            return Result.fail(failedInfo.toString());
        }

        List<String> insertFailedMsg = new ArrayList<>();
        gunImportVos.forEach(gunLicenceImportVo -> {
            GunLicenceVO gunLicenceVO = new GunLicenceVO();
            BeanUtils.copyProperties(gunLicenceImportVo, gunLicenceVO);

            // 设置部门
            gunLicenceVO.setDepartmentId(departmentId);
            // 设置证件有效期
            LocalDate localDate = LocalDate.parse(gunLicenceImportVo.getGunLicenceValidity(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long gunLicenceValidity = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            gunLicenceVO.setGunLicenceValidity(gunLicenceValidity);

            Result result = gunLicenceService.saveGunLicence(gunLicenceVO);
            if (result.isFailed()) {
                insertFailedMsg.add(result.getMsg());
            }
        });
        if (!insertFailedMsg.isEmpty()) {
            throw new Exception(insertFailedMsg.toString());
        }
        return Result.success();
    }

    /**
     * 导入保安证
     * @param file 文件
     * @param departmentId 部门
     * @return 结果
     * @throws Exception 错误
     */
    @Transactional(rollbackFor = Exception.class)
    public Object gunSecurityExcelImport(MultipartFile file, Integer departmentId) throws Exception {
        if (file == null || file.getSize() == 0) {
            return Result.fail("文件不存在");
        }

        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return authResult;
        }

        GunSecurityExcelListener listener = new GunSecurityExcelListener(departmentId);
        // 读取所有Sheet的数据.每次读完一个Sheet就会调用这个方法
        EasyExcel.read(file.getInputStream(), GunSecurityImportVO.class, listener).sheet(0).doRead();
        List<GunSecurityImportVO> gunSecurityImportVoList = listener.getDatas();
        List<String> failedInfo = listener.getFailedInfo();

        // 如果存在错误，需要重新修改
        if (!failedInfo.isEmpty()) {
            return Result.fail(failedInfo.toString());
        }

        List<String> insertFailedMsg = new ArrayList<>();
        gunSecurityImportVoList.forEach(gunSecurityImportVo -> {
            GunSecurityVO gunSecurityVO = new GunSecurityVO();
            BeanUtils.copyProperties(gunSecurityImportVo, gunSecurityVO);

            // 设置部门
            gunSecurityVO.setDepartmentId(departmentId);

            Result result = gunSecurityService.saveGunSecurity(gunSecurityVO);
            if (result.isFailed()) {
                insertFailedMsg.add(result.getMsg());
            }
        });
        if (!insertFailedMsg.isEmpty()) {
            throw new Exception(insertFailedMsg.toString());
        }
        return Result.success();
    }

    public void exportGunLicenceInfo(HttpServletResponse response, GunLicenceQueryVO queryVO) {
        MPJLambdaWrapper<GunLicence> gunMPJLambdaWrapper = gunLicenceService.getGunLicenceMPJLambdaWrapper(queryVO);
        gunLicenceService.exportGunLicenceInfo(response, gunMPJLambdaWrapper);
    }

    class GunLicenceExcelListener extends AnalysisEventListener<GunLicenceImportVO> {
        //自定义用于暂时存储data。
        //可以通过实例获取该值
        private final List<GunLicenceImportVO> datas = new ArrayList<>();
        private final List<String> failedInfo = new ArrayList<>();
        private final Map<String, Integer> gunLicenceNums = new HashMap<>();
        private final Map<String, Integer> empNames = new HashMap<>();
        private int counter = 1;
        private boolean legal = true;

        private final Integer departmentId;

        public GunLicenceExcelListener(int departmentId) {
            this.departmentId = departmentId;
        }

        /**
         * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
         */
        @Override
        public void invoke(GunLicenceImportVO gunLicenceImportVo, AnalysisContext context) {
            boolean b = checkDataLegality(gunLicenceImportVo);
            if (b) {
                datas.add(gunLicenceImportVo);
            }
            counter ++;
        }

        /**
         * 根据业务自行实现该方法
         */
        private boolean checkDataLegality(GunLicenceImportVO gunLicenceImportVo) {
            legal = true;
            // 所有属性去空格
            gunLicenceImportVo.cleanBlankCharacters();

            // 如果是空行，跳过
            if (gunLicenceImportVo.everyAtriEmpty()) {
                return false;
            }

            String gunLicenceNum = gunLicenceImportVo.getGunLicenceNum();
            String gunLicenceValidity = gunLicenceImportVo.getGunLicenceValidity();
            String empName = gunLicenceImportVo.getEmpName();

            // 枪证号是否重复
            GunLicenceVO gunLicenceVO = new GunLicenceVO();
            gunLicenceVO.setDepartmentId(departmentId);
            gunLicenceVO.setGunLicenceNum(gunLicenceNum);

            if (StringUtils.isBlank(gunLicenceNum)) {
                addFailedInfo("枪证号未填写");
            } else if (gunLicenceService.gunLicenceNumExits(gunLicenceVO)) {
                addFailedInfo("\"" + gunLicenceNum + "\"该枪证号已存在");
            } else if (gunLicenceNums.containsKey(gunLicenceNum)){
                addFailedInfo("\"" + gunLicenceNum + "\"该枪证号与" + gunLicenceNums.get(gunLicenceNum) + "行的枪证号相同");
            } else {
                gunLicenceNums.put(gunLicenceNum, counter);
            }

            // 检查证件有效日期格式是否正确
            if (!StringUtils.isBlank(gunLicenceValidity)) {
                // 如果日期格式为yyyy/MM/dd，修改为yyyy-MM-dd
                changeDateFormat(gunLicenceImportVo);
                if (!DateTimeUtil.isLegalDate(gunLicenceValidity)) {
                    addFailedInfo("\"" + gunLicenceValidity + "\"该日期不符合格式，请使用\"2015-12-04\"格式");
                }
            }

            // 人员类型检查
            if (StringUtils.isBlank(empName)) {
                addFailedInfo("员工姓名未填写");
            } else if (empNames.containsKey(empName)) {
                addFailedInfo("表中插入员工姓名重复");
            } else {
                Employee employee = new Employee();
                employee.setEmpName(empName);
                List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
                if (employeeByCondition == null || employeeByCondition.isEmpty()) {
                    addFailedInfo("\"" + empName + "\"未找到该员工");
                } else {
                    Long employeeId = employeeByCondition.get(0).getId();
                    gunLicenceVO.setEmployeeId(employeeId);
                    if (gunLicenceService.gunLicenceEmployeeExits(gunLicenceVO)) {
                        addFailedInfo("\"" + empName + "\"库中已有该员工持枪证信息");
                    } else {
                        gunLicenceImportVo.setEmployeeId(employeeId);
                    }
                }
            }

            return legal;
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        /*
            datas.clear();
            解析结束销毁不用的资源
         */
        }

        public List<GunLicenceImportVO> getDatas() {
            return datas;
        }

        public List<String> getFailedInfo() {
            return failedInfo;
        }

        private void addFailedInfo(String subInfo) {
            String info = "第" + counter + "行数据错误：" + subInfo;
            failedInfo.add(info);
            legal = false;
        }

        public void changeDateFormat(GunLicenceImportVO gunLicenceImportVo) {
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date date = originalFormat.parse(gunLicenceImportVo.getGunLicenceValidity());
                String target = targetFormat.format(date);
                gunLicenceImportVo.setGunLicenceValidity(target);
            } catch (Exception ignored) {
            }
        }
    }

    class GunSecurityExcelListener extends AnalysisEventListener<GunSecurityImportVO> {
        //自定义用于暂时存储data。
        //可以通过实例获取该值
        private final List<GunSecurityImportVO> datas = new ArrayList<>();
        private final List<String> failedInfo = new ArrayList<>();
        private final Map<String, Integer> gunSecurityNums = new HashMap<>();
        private final Map<String, Integer> empNames = new HashMap<>();
        private int counter = 1;
        private boolean legal = true;

        private final Integer departmentId;

        public GunSecurityExcelListener(int departmentId) {
            this.departmentId = departmentId;
        }

        /**
         * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
         */
        @Override
        public void invoke(GunSecurityImportVO gunSecurityImportVo, AnalysisContext context) {
            boolean b = checkDataLegality(gunSecurityImportVo);
            if (b) {
                datas.add(gunSecurityImportVo);
            }
            counter ++;
        }

        /**
         * 根据业务自行实现该方法
         */
        private boolean checkDataLegality(GunSecurityImportVO gunSecurityImportVo) {
            legal = true;
            // 所有属性去空格
            gunSecurityImportVo.cleanBlankCharacters();

            // 如果是空行，跳过
            if (gunSecurityImportVo.everyAtriEmpty()) {
                return false;
            }

            String securityNum = gunSecurityImportVo.getSecurityNum();
            String empName = gunSecurityImportVo.getEmpName();

            // 保安证号是否重复
            GunSecurityVO gunSecurityVO = new GunSecurityVO();
            gunSecurityVO.setDepartmentId(departmentId);
            gunSecurityVO.setSecurityNum(securityNum);

            if (StringUtils.isBlank(securityNum)) {
                addFailedInfo("保安号未填写");
            } else if (gunSecurityService.gunSecurityNumExits(gunSecurityVO)) {
                addFailedInfo("\"" + securityNum + "\"该保安证号已存在");
            } else if (gunSecurityNums.containsKey(securityNum)){
                addFailedInfo("\"" + securityNum + "\"该保安证号与" + gunSecurityNums.get(securityNum) + "行保安证号相同");
            } else {
                gunSecurityNums.put(securityNum, counter);
            }

            // 人员类型检查
            if (StringUtils.isBlank(empName)) {
                addFailedInfo("员工姓名未填写");
            } else if (empNames.containsKey(empName)) {
                addFailedInfo("表中插入员工姓名重复");
            } else {
                Employee employee = new Employee();
                employee.setEmpName(empName);
                List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
                if (employeeByCondition == null || employeeByCondition.isEmpty()) {
                    addFailedInfo("\"" + empName + "\"未找到该员工");
                } else {
                    Long employeeId = employeeByCondition.get(0).getId();
                    gunSecurityVO.setEmployeeId(employeeId);
                    if (gunSecurityService.gunSecurityEmployeeExits(gunSecurityVO)) {
                        addFailedInfo("\"" + empName + "\"库中已有该员工持枪证信息");
                    } else {
                        gunSecurityImportVo.setEmployeeId(employeeId);
                    }
                }
            }

            return legal;
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        /*
            datas.clear();
            解析结束销毁不用的资源
         */
        }

        public List<GunSecurityImportVO> getDatas() {
            return datas;
        }

        public List<String> getFailedInfo() {
            return failedInfo;
        }

        private void addFailedInfo(String subInfo) {
            String info = "第" + counter + "行数据错误：" + subInfo;
            failedInfo.add(info);
            legal = false;
        }
    }
}
