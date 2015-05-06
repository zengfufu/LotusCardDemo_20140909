package cc.lotuscard;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

public class LotusCardDriver {

	// 寻卡请求类型 _eRequestType
	public final static int RT_ALL = 0x52; // /< 符合14443A卡片
	public final static int RT_NOT_HALT = 0x26; // /< 未进入休眠状态的卡
	// 密钥验证模式 _eAuthMode
	public final static int AM_A = 0x60; // /< 验证A密码
	public final static int AM_B = 0x61; // /< 验证B密码

	/*********************** 以下对象要外部要赋值 **************************************/
	public static UsbDeviceConnection m_UsbDeviceConnection = null;
	public static UsbEndpoint m_InEndpoint = null;
	public static UsbEndpoint m_OutEndpoint = null;

	/*************************************************************/
	public LotusCardDriver() {
	}

	static {
		System.loadLibrary("LotusCardDriver");
	}

	/**
	 * 打开设备
	 * 
	 * @param strDeviceName
	 *            串口设备名称
	 * @param nVID
	 *            USB设备VID
	 * @param nPID
	 *            USB设备PID
	 * @param bUseExendReadWrite
	 *            是否使用外部读写通道 如果没有设备写权限时，可以使用外部USB或串口进行通讯，
	 *            需要改造callBackProcess中相关代码完成读写工作 目前范例提供USB操作
	 * @return 设备句柄
	 */
	public native int OpenDevice(String strDeviceName, int nVID, int nPID,
			boolean bUseExendReadWrite);

	/**
	 * 关闭设备
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 */
	public native void CloseDevice(int nDeviceHandle);

	/**
	 * 寻卡
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nRequestType
	 *            请求类型
	 * @param tLotusCardParam
	 *            结果值 用里面的卡片类型
	 * @return true = 成功
	 */
	public native boolean Request(int nDeviceHandle, int nRequestType,
			LotusCardParam tLotusCardParam);

	/**
	 * 防冲突
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param tLotusCardParam
	 *            结果值 用里面的卡号
	 * @return true = 成功
	 */
	public native boolean Anticoll(int nDeviceHandle,
			LotusCardParam tLotusCardParam);

	/**
	 * 选卡
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param tLotusCardParam
	 *            参数(使用里面的卡号)与结果值(使用里面的卡容量大小)
	 * @return true = 成功
	 */
	public native boolean Select(int nDeviceHandle,
			LotusCardParam tLotusCardParam);

	/**
	 * 密钥验证
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAuthMode
	 *            验证模式
	 * @param nSectionIndex
	 *            扇区索引
	 * @param tLotusCardParam
	 *            参数(使用里面的卡号)
	 * @return true = 成功
	 */
	public native boolean Authentication(int nDeviceHandle, int nAuthMode,
			int nSectionIndex, LotusCardParam tLotusCardParam);

	/**
	 * 卡片中止响应
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @return true = 成功
	 */
	public native boolean Halt(int nDeviceHandle);

	/**
	 * 读指定地址数据
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAddress
	 *            块地址
	 * @param tLotusCardParam
	 *            结果值（读写缓冲）
	 * @return true = 成功
	 */
	public native boolean Read(int nDeviceHandle, int nAddress,
			LotusCardParam tLotusCardParam);

	/**
	 * 写指定地址数据
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAddress
	 *            块地址
	 * @param tLotusCardParam
	 *            参数（读写缓冲）
	 * @return true = 成功
	 */
	public native boolean Write(int nDeviceHandle, int nAddress,
			LotusCardParam tLotusCardParam);

	/**
	 * 加值
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAddress
	 *            块地址
	 * @param nValue
	 *            值
	 * @return true = 成功
	 */
	public native boolean Increment(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * 减值
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAddress
	 *            块地址
	 * @param nValue
	 *            值
	 * @return true = 成功
	 */
	public native boolean Decreament(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * 装载密钥
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAuthMode
	 *            验证模式
	 * @param nSectionIndex
	 *            扇区索引
	 * @param tLotusCardParam
	 *            参数（密钥）
	 * @return true = 成功
	 */
	public native boolean LoadKey(int nDeviceHandle, int nAuthMode,
			int nSectionIndex, LotusCardParam tLotusCardParam);

	/**
	 * 蜂鸣
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nBeepLen
	 *            蜂鸣长度 毫秒为单位
	 * @return true = 成功
	 */
	public native boolean Beep(int nDeviceHandle, int nBeepLen);

	/**
	 * 发送指令 用于CPU卡
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nTimeOut
	 *            超时参数
	 * @param tLotusCardParam
	 *            参数（指令缓冲,返回结果）
	 * @return true = 成功
	 */
	public native boolean SendCpuCommand(int nDeviceHandle, int nTimeOut,
			LotusCardParam tLotusCardParam);

	/******************************** 以下函数调用上述函数，为了简化第三方调用操作 ***************************/
	/**
	 * 获取卡号
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nRequestType
	 *            请求类型
	 * @param tLotusCardParam
	 *            结果值
	 * @return true = 成功
	 */
	public native boolean GetCardNo(int nDeviceHandle, int nRequestType,
			LotusCardParam tLotusCardParam);

	/**
	 * 初始值
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param nAddress
	 *            块地址
	 * @param nValue
	 *            值
	 * @return true = 成功
	 */
	public native boolean InitValue(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * 修改密码AB
	 * 
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param pPasswordA
	 *            密码A
	 * @param pPasswordB
	 *            密码B
	 * @return true = 成功
	 */
	public native boolean ChangePassword(int nDeviceHandle, int nSectionIndex,
			String strPasswordA, String strPasswordB);


	/**
	 * 复位CPU卡
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param tLotusCardParam
	 *            结果值 
	 * @return true = 成功
	 */
	public native boolean ResetCpuCard(int nDeviceHandle, LotusCardParam tLotusCardParam);

	/**
	 * 发送指令 用于CPU卡 封装LotusCardSendCpuCommand
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param tLotusCardParam 参数（指令缓冲,返回结果）
	 * @return true = 成功
	 */
	public native boolean SendCOSCommand(int nDeviceHandle, LotusCardParam tLotusCardParam);	

	/**
	 * 通过回调处理数据读写
	 * 
	 * @param bRead
	 *            是否读操作
	 * @param arrBuffer
	 *            缓冲
	 * @return true = 操作成功
	 */
	public static boolean callBackProcess(boolean bRead, byte[] arrBuffer) {
		int nResult = 0;
		boolean bResult = false;
		int nBufferLength = arrBuffer.length;
		int nWaitCount = 0;
		if (null == m_UsbDeviceConnection)
			return false;
		if (null == m_OutEndpoint)
			return false;
		if (null == m_InEndpoint)
			return false;
		if (nBufferLength != 64)
			return false;
		if (true == bRead) {
			arrBuffer[0] = 0;
			while (true) {
				nResult = m_UsbDeviceConnection.bulkTransfer(m_InEndpoint,
						arrBuffer, nBufferLength, 3000);
				if (nResult <= 0)
					break;
				if (arrBuffer[0] != 0)
					break;
				nWaitCount++;
				if (nWaitCount > 1000)
					break;
			}
			if (nResult == nBufferLength) {
				bResult = true;
			} else {
				bResult = false;
			}
		} else {
			nResult = m_UsbDeviceConnection.bulkTransfer(m_OutEndpoint,
					arrBuffer, nBufferLength, 3000);
			if (nResult == nBufferLength) {
				bResult = true;
			} else {
				bResult = false;
			}
		}
		return bResult;
	}
}
