package cc.lotuscard;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

public class LotusCardDriver {

	// Ѱ���������� _eRequestType
	public final static int RT_ALL = 0x52; // /< ����14443A��Ƭ
	public final static int RT_NOT_HALT = 0x26; // /< δ��������״̬�Ŀ�
	// ��Կ��֤ģʽ _eAuthMode
	public final static int AM_A = 0x60; // /< ��֤A����
	public final static int AM_B = 0x61; // /< ��֤B����

	/*********************** ���¶���Ҫ�ⲿҪ��ֵ **************************************/
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
	 * ���豸
	 * 
	 * @param strDeviceName
	 *            �����豸����
	 * @param nVID
	 *            USB�豸VID
	 * @param nPID
	 *            USB�豸PID
	 * @param bUseExendReadWrite
	 *            �Ƿ�ʹ���ⲿ��дͨ�� ���û���豸дȨ��ʱ������ʹ���ⲿUSB�򴮿ڽ���ͨѶ��
	 *            ��Ҫ����callBackProcess����ش�����ɶ�д���� Ŀǰ�����ṩUSB����
	 * @return �豸���
	 */
	public native int OpenDevice(String strDeviceName, int nVID, int nPID,
			boolean bUseExendReadWrite);

	/**
	 * �ر��豸
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 */
	public native void CloseDevice(int nDeviceHandle);

	/**
	 * Ѱ��
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nRequestType
	 *            ��������
	 * @param tLotusCardParam
	 *            ���ֵ ������Ŀ�Ƭ����
	 * @return true = �ɹ�
	 */
	public native boolean Request(int nDeviceHandle, int nRequestType,
			LotusCardParam tLotusCardParam);

	/**
	 * ����ͻ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param tLotusCardParam
	 *            ���ֵ ������Ŀ���
	 * @return true = �ɹ�
	 */
	public native boolean Anticoll(int nDeviceHandle,
			LotusCardParam tLotusCardParam);

	/**
	 * ѡ��
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param tLotusCardParam
	 *            ����(ʹ������Ŀ���)����ֵ(ʹ������Ŀ�������С)
	 * @return true = �ɹ�
	 */
	public native boolean Select(int nDeviceHandle,
			LotusCardParam tLotusCardParam);

	/**
	 * ��Կ��֤
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAuthMode
	 *            ��֤ģʽ
	 * @param nSectionIndex
	 *            ��������
	 * @param tLotusCardParam
	 *            ����(ʹ������Ŀ���)
	 * @return true = �ɹ�
	 */
	public native boolean Authentication(int nDeviceHandle, int nAuthMode,
			int nSectionIndex, LotusCardParam tLotusCardParam);

	/**
	 * ��Ƭ��ֹ��Ӧ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @return true = �ɹ�
	 */
	public native boolean Halt(int nDeviceHandle);

	/**
	 * ��ָ����ַ����
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAddress
	 *            ���ַ
	 * @param tLotusCardParam
	 *            ���ֵ����д���壩
	 * @return true = �ɹ�
	 */
	public native boolean Read(int nDeviceHandle, int nAddress,
			LotusCardParam tLotusCardParam);

	/**
	 * дָ����ַ����
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAddress
	 *            ���ַ
	 * @param tLotusCardParam
	 *            ��������д���壩
	 * @return true = �ɹ�
	 */
	public native boolean Write(int nDeviceHandle, int nAddress,
			LotusCardParam tLotusCardParam);

	/**
	 * ��ֵ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAddress
	 *            ���ַ
	 * @param nValue
	 *            ֵ
	 * @return true = �ɹ�
	 */
	public native boolean Increment(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * ��ֵ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAddress
	 *            ���ַ
	 * @param nValue
	 *            ֵ
	 * @return true = �ɹ�
	 */
	public native boolean Decreament(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * װ����Կ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAuthMode
	 *            ��֤ģʽ
	 * @param nSectionIndex
	 *            ��������
	 * @param tLotusCardParam
	 *            ��������Կ��
	 * @return true = �ɹ�
	 */
	public native boolean LoadKey(int nDeviceHandle, int nAuthMode,
			int nSectionIndex, LotusCardParam tLotusCardParam);

	/**
	 * ����
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nBeepLen
	 *            �������� ����Ϊ��λ
	 * @return true = �ɹ�
	 */
	public native boolean Beep(int nDeviceHandle, int nBeepLen);

	/**
	 * ����ָ�� ����CPU��
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nTimeOut
	 *            ��ʱ����
	 * @param tLotusCardParam
	 *            ������ָ���,���ؽ����
	 * @return true = �ɹ�
	 */
	public native boolean SendCpuCommand(int nDeviceHandle, int nTimeOut,
			LotusCardParam tLotusCardParam);

	/******************************** ���º�����������������Ϊ�˼򻯵��������ò��� ***************************/
	/**
	 * ��ȡ����
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nRequestType
	 *            ��������
	 * @param tLotusCardParam
	 *            ���ֵ
	 * @return true = �ɹ�
	 */
	public native boolean GetCardNo(int nDeviceHandle, int nRequestType,
			LotusCardParam tLotusCardParam);

	/**
	 * ��ʼֵ
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param nAddress
	 *            ���ַ
	 * @param nValue
	 *            ֵ
	 * @return true = �ɹ�
	 */
	public native boolean InitValue(int nDeviceHandle, int nAddress, int nValue);

	/**
	 * �޸�����AB
	 * 
	 * @param nDeviceHandle
	 *            �豸���
	 * @param pPasswordA
	 *            ����A
	 * @param pPasswordB
	 *            ����B
	 * @return true = �ɹ�
	 */
	public native boolean ChangePassword(int nDeviceHandle, int nSectionIndex,
			String strPasswordA, String strPasswordB);


	/**
	 * ��λCPU��
	 * @param nDeviceHandle
	 *            �豸���
	 * @param tLotusCardParam
	 *            ���ֵ 
	 * @return true = �ɹ�
	 */
	public native boolean ResetCpuCard(int nDeviceHandle, LotusCardParam tLotusCardParam);

	/**
	 * ����ָ�� ����CPU�� ��װLotusCardSendCpuCommand
	 * @param nDeviceHandle
	 *            �豸���
	 * @param tLotusCardParam ������ָ���,���ؽ����
	 * @return true = �ɹ�
	 */
	public native boolean SendCOSCommand(int nDeviceHandle, LotusCardParam tLotusCardParam);	

	/**
	 * ͨ���ص��������ݶ�д
	 * 
	 * @param bRead
	 *            �Ƿ������
	 * @param arrBuffer
	 *            ����
	 * @return true = �����ɹ�
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
