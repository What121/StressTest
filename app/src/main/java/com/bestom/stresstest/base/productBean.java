package com.bestom.stresstest.base;

public class productBean {
    private String firmVersion="";      //固件版本
    private String cpu="";              //cpu型号
    private String ddr="";              //ddr
    private String sn="";               //sn
    private String emmc="";             //内置存储
    private int tp=0;                   //触屏 1?pass:no
    private int wifi=0;
    private int bt=0;
    private int sd=0;
    private int usb=0;
    private int leftsound=0;
    private int rightsound=0;
    private int micro=0;
    private int powerkey=0;
    private int vpkey=0;
    private int vdkey=0;
    private int rgb=0;
    private int gsensor=0;
    private int lightsensor=0;
    private int nfc=0;
    private int nfclight=0;
    private int frontcamera=0;
    private int backcamera=0;

    private String time ="";            //测试上传时间

    public productBean() {
        new productBean( firmVersion,  cpu,  ddr,  sn,  emmc,  tp,  wifi,  bt,  sd,  usb,  leftsound,  rightsound,  micro,  powerkey,  vpkey,  vdkey,  rgb,  gsensor,  lightsensor,  nfc,  nfclight,  frontcamera,  backcamera ,time);
    }

    private productBean(String firmVersion, String cpu, String ddr, String sn, String emmc, int tp, int wifi, int bt, int sd, int usb, int leftsound, int rightsound, int micro, int powerkey, int vpkey, int vdkey, int rgb, int gsensor, int lightsensor, int nfc, int nfclight, int frontcamera, int backcamera , String time) {
        this.firmVersion = firmVersion;
        this.cpu = cpu;
        this.ddr = ddr;
        this.sn = sn;
        this.emmc = emmc;
        this.tp = tp;
        this.wifi = wifi;
        this.bt = bt;
        this.sd = sd;
        this.usb = usb;
        this.leftsound = leftsound;
        this.rightsound = rightsound;
        this.micro = micro;
        this.powerkey = powerkey;
        this.vpkey = vpkey;
        this.vdkey = vdkey;
        this.rgb = rgb;
        this.gsensor = gsensor;
        this.lightsensor = lightsensor;
        this.nfc = nfc;
        this.nfclight = nfclight;
        this.frontcamera = frontcamera;
        this.backcamera = backcamera;
        this.time=time;
    }

    //    private productBean(String firmVersion, String cpu, String ddr, String sn, String emmc, int tp, int wifi, int bt, int sd, int usb, int leftsound, int rightsound, int micro, int powerkey, int vpkey, int vdkey, int rgb, int gsensor, int lightsensor, int nfc, int nfclight, int frontcamera, int backcamera) {
//        this.firmVersion = firmVersion;
//        this.cpu = cpu;
//        this.ddr = ddr;
//        this.sn = sn;
//        this.emmc = emmc;
//        this.tp = tp;
//        this.wifi = wifi;
//        this.bt = bt;
//        this.sd = sd;
//        this.usb = usb;
//        this.leftsound = leftsound;
//        this.rightsound = rightsound;
//        this.micro = micro;
//        this.powerkey = powerkey;
//        this.vpkey = vpkey;
//        this.vdkey = vdkey;
//        this.rgb = rgb;
//        this.gsensor = gsensor;
//        this.lightsensor = lightsensor;
//        this.nfc = nfc;
//        this.nfclight = nfclight;
//        this.frontcamera = frontcamera;
//        this.backcamera = backcamera;
//    }

    public int getFrontcamera() {
        return frontcamera;
    }

    public void setFrontcamera(int frontcamera) {
        this.frontcamera = frontcamera;
    }

    public int getBackcamera() {
        return backcamera;
    }

    public void setBackcamera(int backcamera) {
        this.backcamera = backcamera;
    }

    public String getFirmVersion() {
        return firmVersion;
    }

    public void setFirmVersion(String firmVersion) {
        this.firmVersion = firmVersion;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getDdr() {
        return ddr;
    }

    public void setDdr(String ddr) {
        this.ddr = ddr;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getEmmc() {
        return emmc;
    }

    public void setEmmc(String emmc) {
        this.emmc = emmc;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public int getBt() {
        return bt;
    }

    public void setBt(int bt) {
        this.bt = bt;
    }

    public int getSd() {
        return sd;
    }

    public void setSd(int sd) {
        this.sd = sd;
    }

    public int getUsb() {
        return usb;
    }

    public void setUsb(int usb) {
        this.usb = usb;
    }

    public int getLeftsound() {
        return leftsound;
    }

    public void setLeftsound(int leftsound) {
        this.leftsound = leftsound;
    }

    public int getRightsound() {
        return rightsound;
    }

    public void setRightsound(int rightsound) {
        this.rightsound = rightsound;
    }

    public int getMicro() {
        return micro;
    }

    public void setMicro(int micro) {
        this.micro = micro;
    }

    public int getPowerkey() {
        return powerkey;
    }

    public void setPowerkey(int powerkey) {
        this.powerkey = powerkey;
    }

    public int getVpkey() {
        return vpkey;
    }

    public void setVpkey(int vpkey) {
        this.vpkey = vpkey;
    }

    public int getVdkey() {
        return vdkey;
    }

    public void setVdkey(int vdkey) {
        this.vdkey = vdkey;
    }

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public int getGsensor() {
        return gsensor;
    }

    public void setGsensor(int gsensor) {
        this.gsensor = gsensor;
    }

    public int getLightsensor() {
        return lightsensor;
    }

    public void setLightsensor(int lightsensor) {
        this.lightsensor = lightsensor;
    }

    public int getNfc() {
        return nfc;
    }

    public void setNfc(int nfc) {
        this.nfc = nfc;
    }

    public int getNfclight() {
        return nfclight;
    }

    public void setNfclight(int nfclight) {
        this.nfclight = nfclight;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
