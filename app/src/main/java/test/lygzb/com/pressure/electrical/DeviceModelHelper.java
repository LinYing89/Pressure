package test.lygzb.com.pressure.electrical;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;

/**
 * 设置设备模式时，临时保存的数据
 * Created by linqiang on 2017/3/16.
 */

public class DeviceModelHelper {

    private Device devToSet;
    private EDeviceModel toDeviceModel;
    private String order;

    /**
     * 设置的设备
     * @return
     */
    public Device getDevToSet() {
        return devToSet;
    }

    /**
     * 设置的设备
     * @param devToSet
     */
    public void setDevToSet(Device devToSet) {
        this.devToSet = devToSet;
    }

    /**
     * 设置为的模式
     * @return
     */
    public EDeviceModel getToDeviceModel() {
        return toDeviceModel;
    }

    /**
     * 设置为的模式
     * @param toDeviceModel
     */
    public void setToDeviceModel(EDeviceModel toDeviceModel) {
        this.toDeviceModel = toDeviceModel;
    }

    /**
     * 设置报文
     * @return
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置报文
     * @param order
     */
    public void setOrder(String order) {
        this.order = order;
    }
}
