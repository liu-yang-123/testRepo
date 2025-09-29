package com.zcxd.gun.service.server;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.zcxd.gun.common.GunTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.model.GunCategory;
import com.zcxd.gun.db.model.GunLicence;
import com.zcxd.gun.feign.RemoteProvider;
import com.zcxd.gun.service.*;
import com.zcxd.gun.vo.GunImportVO;
import com.zcxd.gun.vo.GunLicenceVO;
import com.zcxd.gun.vo.GunMaintainRecordVO;
import com.zcxd.gun.vo.GunVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author zccc
 */
@Service
public class GunServer {
    @Autowired
    GunService gunService;
    @Autowired
    RemoteProvider remoteService;
    @Autowired
    GunMaintainRecordService gunMaintainRecordService;
    @Autowired
    GunCategoryService gunCategoryService;
    @Autowired
    GunLicenceService gunLicenceService;

    /**
     * 新增
     * @param gunVo 属性
     * @return 结果
     */
    public Result saveGun(GunVO gunVo) {
        Result result = checkGunAttr(gunVo);
        if (result.isFailed()) {
            return result;
        }
        return gunService.saveGun(gunVo);
    }

    /**
     * 检查枪属性是否合法
     * @param gunVo vo
     * @return 检查结果
     */
    private Result checkGunAttr(GunVO gunVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(gunVo.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }

        // 枪弹编号
        if (!StringUtils.isBlank(gunVo.getGunCode()) && gunService.gunCodeExits(gunVo)) {
            return Result.fail("枪弹编号已存在，请重新填写！");
        }

        // 持枪证
        String gunLicenceNum = gunVo.getGunLicenceNum();
        if (!StringUtils.isBlank(gunLicenceNum)) {
            if (gunService.gunLicenceNumExits(gunVo)) {
                return Result.fail("该持枪证号已关联其他枪支，请重新填写！");
            } else {
                List<GunLicence> licences = gunLicenceService.getGunLicenceByLicenceNum(new GunLicenceVO(gunLicenceNum, gunVo.getDepartmentId()));
                if (licences.isEmpty()) {
                    return Result.fail("未查找到该持枪证号，请重新填写！");
                }
                GunLicence licence = licences.get(0);
                gunVo.setGunLicenceId(licence.getId());
            }
        }

        // 枪弹类型
        GunCategory category = gunCategoryService.getById(gunVo.getGunCategory());
        if (category == null) {
            return Result.fail("枪支类型错误，请重新填写！");
        }

        // 枪弹类型为枪支时要填写枪证号
        if (category.getGunType() == GunTypeEnum.GUN.getValue() && StringUtils.isBlank(gunLicenceNum)) {
            return Result.fail("枪支类型需要填写对应的枪证号！");
        }

        return Result.success();
    }

    /**
     * 更新枪信息
     * @param gunVo 属性
     * @return 结果
     */
    public Object updateGunById(GunVO gunVo) {
        Result result = checkGunAttr(gunVo);
        if (result.isFailed()) {
            return result;
        }
        return gunService.updateGunById(gunVo);
    }

    /**
     * 停用
     * @param gunVO 枪属性
     * @return 结果
     */
    public Object gunStopOperate(GunVO gunVO) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(gunVO.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }
        return gunService.stop(gunVO.getId());
    }

    /**
     * 分解
     * @param gunVO 枪属性
     * @return 结果
     */
    public Object gunCheckOperate(GunVO gunVO) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(gunVO.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }
        if (!gunCategoryService.isGun(gunVO.getGunCategory())) {
            return Result.fail("非枪支不能分解");
        }

        Result check = gunService.check(gunVO.getId());
        if (check.isFailed()) {
            return check;
        }
        // 添加记录
        GunMaintainRecordVO gunMaintainRecordVo = new GunMaintainRecordVO();
        gunMaintainRecordVo.setGunId(gunVO.getId());
        gunMaintainRecordVo.setOperateType(2);
        gunMaintainRecordVo.setDepartmentId(gunVO.getDepartmentId());
        gunMaintainRecordVo.setRemark(gunVO.getRemark());
        return gunMaintainRecordService.saveGunMaintainRecord(gunMaintainRecordVo);
    }

    /**
     * 清洁
     * @param gunVO 枪属性
     * @return 结果
     */
    public Object gunCleanOperate(GunVO gunVO) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(gunVO.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }
        if (gunCategoryService.isGun(gunVO.getGunCategory())) {
            return Result.fail("非枪支不能清洁");
        }

        Result clean = gunService.clean(gunVO.getId());
        if (clean.isFailed()) {
            return clean;
        }
        // 添加记录
        GunMaintainRecordVO gunMaintainRecordVo = new GunMaintainRecordVO();
        gunMaintainRecordVo.setGunId(gunVO.getId());
        gunMaintainRecordVo.setOperateType(1);
        gunMaintainRecordVo.setDepartmentId(gunVO.getDepartmentId());
        gunMaintainRecordVo.setRemark(gunVO.getRemark());
        return gunMaintainRecordService.saveGunMaintainRecord(gunMaintainRecordVo);
    }

    /**
     * 导入
     * @param file 枪信息文件
     * @param departmentId 部门
     * @return 结果
     * @throws Exception 错误
     */
    @Transactional(rollbackFor = Exception.class)
    public Object gunExcelImport(MultipartFile file, Integer departmentId) throws Exception {
        if (file == null || file.getSize() == 0) {
            return Result.fail("文件不存在");
        }
        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return authResult;
        }

        Map<String, Long> categoryMap = new HashMap<>();
        gunCategoryService.list().forEach(gunCategory -> categoryMap.put(gunCategory.getGunCategoryName(), gunCategory.getId()));

        ExcelListener listener = new ExcelListener(departmentId);
        // 读取所有Sheet的数据.每次读完一个Sheet就会调用这个方法
        EasyExcel.read(file.getInputStream(), GunImportVO.class, listener).sheet(0).doRead();
        List<GunImportVO> gunImportVos = listener.getDatas();
        List<String> failedInfo = listener.getFailedInfo();
        Set<String> illegalGunCategory = new HashSet<>();

        // 如果存在错误，需要重新修改
        if (!failedInfo.isEmpty()) {
            return Result.fail(failedInfo.toString());
        }

        // 检查枪支类型是否正确
        for (int i = 0; i < gunImportVos.size(); i++) {
            GunImportVO gunImportVo = gunImportVos.get(i);
            String gunCategory = gunImportVo.getGunCategory();
            if (!categoryMap.containsKey(gunCategory)) {
                if (!illegalGunCategory.contains(gunCategory)) {
                    failedInfo.add("枪支类型未录入：" + gunCategory);
                    illegalGunCategory.add(gunCategory);
                }
            }
        }

        if (gunImportVos == null || gunImportVos.isEmpty()) {
            return Result.fail("未读取到枪弹信息内容");
        }

        List<String> insertFailedMsg = new ArrayList<>();
        gunImportVos.forEach(gunImportVo -> {
            GunVO gunVo = new GunVO();
            BeanUtils.copyProperties(gunImportVo, gunVo);
            gunVo.setGunCategory(categoryMap.get(gunImportVo.getGunCategory()));
            // 设置部门
            gunVo.setDepartmentId(departmentId);

            Result result = saveGun(gunVo);
            if (result.isFailed()) {
                insertFailedMsg.add(result.getMsg());
            }
        });
        if (!insertFailedMsg.isEmpty()) {
            throw new Exception(insertFailedMsg.toString());
        }
        return Result.success();
    }

    private void guntaskMotion(GunVO gunVO) {
        if (gunVO instanceof GunVO) {

        }
    }

    class ExcelListener extends AnalysisEventListener<GunImportVO> {
        //自定义用于暂时存储data。
        //可以通过实例获取该值
        private final List<GunImportVO> datas = new ArrayList<>();
        private final List<String> failedInfo = new ArrayList<>();
        private final Map<String, Integer> gunCodes = new HashMap<>();
        private final Map<String, Integer> gunLicenses = new HashMap<>();
        private int counter = 1;
        private boolean legal = true;

        private Integer departmentId;

        public ExcelListener(int departmentId) {
            this.departmentId = departmentId;
        }

        /**
         * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
         */
        @Override
        public void invoke(GunImportVO gunImportVo, AnalysisContext context) {
            boolean b = checkDataLegality(gunImportVo);
            if (b) {
                datas.add(gunImportVo);
            }
            counter ++;
        }

        /**
         * 根据业务自行实现该方法
         */
        private boolean checkDataLegality(GunImportVO gunImportVo) {
            legal = true;
            // 所有属性去空格
            gunImportVo.cleanBlankCharacters();

            // 如果是空行，跳过
            if (gunImportVo.everyAtriEmpty()) {
                return false;
            }

            // 枪证、枪号是否重复
            GunVO gunVo = new GunVO();
            gunVo.setDepartmentId(departmentId);
            gunVo.setGunCode(gunImportVo.getGunCode());
            gunVo.setGunLicenceNum(gunImportVo.getGunLicenceNum());

            if (StringUtils.isBlank(gunImportVo.getGunCode())) {
                addFailedInfo("枪号未填写");
            } else if (gunService.gunCodeExits(gunVo)) {
                addFailedInfo("\"" + gunImportVo.getGunCode() + "\"该枪号已存在");
            } else if (gunCodes.containsKey(gunImportVo.getGunCode())){
                addFailedInfo("\"" + gunImportVo.getGunCode() + "\"该枪号与" + gunCodes.get(gunImportVo.getGunCode()) + "行的枪号相同");
            } else {
                gunCodes.put(gunImportVo.getGunCode(), counter);
            }

            GunLicenceVO gunLicenceVO = new GunLicenceVO(gunVo.getGunLicenceNum(), gunVo.getDepartmentId());
            List<GunLicence> gunLicenceByLicenceNum = gunLicenceService.getGunLicenceByLicenceNum(gunLicenceVO);
            if (StringUtils.isBlank(gunImportVo.getGunLicenceNum())) {
                addFailedInfo("枪证号未填写");
            } else if (gunLicenceByLicenceNum.isEmpty()) {
                addFailedInfo("\"" + gunImportVo.getGunLicenceNum() + "\"该枪证号不存在");
            } else if (gunLicenses.containsKey(gunImportVo.getGunLicenceNum())){
                addFailedInfo("\"" + gunImportVo.getGunLicenceNum() + "\"该枪证号与" + gunLicenses.get(gunImportVo.getGunLicenceNum()) + "行的枪证号相同");
            } else {
                gunLicenses.put(gunImportVo.getGunLicenceNum(), counter);
                gunVo.setGunLicenceId(gunLicenceByLicenceNum.get(0).getId());
            }

            // 检查购买日期格式是否正确
            if (!StringUtils.isBlank(gunImportVo.getBuyDate())) {
                // 如果日期格式为yyyy/MM/dd，修改为yyyy-MM-dd
                changeDateFormat(gunImportVo);
                if (!DateTimeUtil.isLegalDate(gunImportVo.getBuyDate())) {
                    addFailedInfo("\"" + gunImportVo.getBuyDate() + "\"该日期不符合格式，请使用\"2015-12-04\"格式");
                }
            }

            // 枪支类型检查
            if (StringUtils.isBlank(gunImportVo.getGunType())) {
                addFailedInfo("枪弹类型未填写");
            } else if (!GunTypeEnum.containValue(gunImportVo.getGunType())){
                addFailedInfo("\"" + gunImportVo.getGunType() + "\"无效枪弹类型");
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

        public List<GunImportVO> getDatas() {
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

        public void changeDateFormat(GunImportVO gunImportVo) {
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date date = originalFormat.parse(gunImportVo.getBuyDate());
                String target = targetFormat.format(date);
                gunImportVo.setBuyDate(target);
            } catch (Exception ignored) {
            }
        }
    }
}
