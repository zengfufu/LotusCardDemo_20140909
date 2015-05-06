package cc.lotuscard;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LotusCardDemoActivity extends Activity {
	private LotusCardDriver mLotusCardDriver;

	private UsbManager m_UsbManager = null;
	private UsbDevice m_LotusCardDevice = null;
	private UsbInterface m_LotusCardInterface = null;
	private UsbDeviceConnection m_LotusCardDeviceConnection = null;
	private final int m_nVID = 1306;
	private final int m_nPID = 20763;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	private Boolean m_bUseUsbHostApi = true;
	private Boolean m_bCanUseUsbHostApi = true;
	private String m_strDeviceNode;

	private int m_nDeviceHandle = -1;
	private Handler m_Handler = null;
	private CardOperateThread m_CardOperateThread;
	private Boolean m_bCardOperateThreadRunning = false;
	/*********************************** UI *********************************/
	private Button m_btnTest;
	private Button m_btnAutoTest;
	private CheckBox m_chkUseUsbHostApi;
	private EditText m_edtLog;
	private TextView m_tvDeviceNode;
	private TextView m_tvMessage;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if ((m_nDeviceHandle != -1) && (null != m_CardOperateThread)) {
			if (true == m_bCardOperateThreadRunning) {
				m_CardOperateThread.cancel();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			m_bCardOperateThreadRunning = !m_bCardOperateThreadRunning;

		}		
		if (-1 != m_nDeviceHandle)
			mLotusCardDriver.CloseDevice(m_nDeviceHandle);		
		super.onDestroy();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		m_btnTest = (Button) findViewById(R.id.btnTest);
		m_btnAutoTest = (Button) findViewById(R.id.btnAutoTest);
		m_edtLog = (EditText) findViewById(R.id.edtLog);
		m_tvDeviceNode = (TextView) findViewById(R.id.tvDeviceNode);
		m_tvMessage = (TextView) findViewById(R.id.tvMessage);
		m_edtLog.setText("");

		m_chkUseUsbHostApi = (CheckBox) findViewById(R.id.chkUseUsbHostApi);

		// 设置USB读写回调 串口可以不用此操作
		m_bCanUseUsbHostApi = SetUsbCallBack();
		if (m_bCanUseUsbHostApi) {
			AddLog("Find LotusSmart IC Reader!");
			m_tvDeviceNode.setText("Device Node:" + m_strDeviceNode);
		} else {
			AddLog("Not Find LotusSmart IC Reader!");
		}
		m_chkUseUsbHostApi.setChecked(m_bCanUseUsbHostApi);

		// LotusCardParam tLotusCardParam = new LotusCardParam();
		mLotusCardDriver = new LotusCardDriver();
		// int nDeviceHandle = mLotusCardDriver.OpenDevice("/dev/ttyTCC1", 0,
		// 0);
		// int nDeviceHandle = mLotusCardDriver.OpenDevice("/dev/ttyS3", 0, 0);

		m_Handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				AddLog(msg.obj.toString());
				super.handleMessage(msg);
			}
		};

	}

	public void OnTestListener(View arg0) {
		if (null == mLotusCardDriver)
			return;
		if ((m_nDeviceHandle != -1) && (null != m_CardOperateThread)) {
			if (true == m_bCardOperateThreadRunning) {
				m_CardOperateThread.cancel();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			m_bCardOperateThreadRunning = !m_bCardOperateThreadRunning;
			m_tvMessage.setText("Message:");
			m_btnAutoTest.setText("AutoTest");
		}			
		if (m_nDeviceHandle == -1)
		{
			m_nDeviceHandle = mLotusCardDriver.OpenDevice("", 0, 0,
				m_chkUseUsbHostApi.isChecked());
		}
		if (m_nDeviceHandle != -1) {
			AddLog("Open Device Success!");
			testIcCardReader(m_nDeviceHandle);
//			mLotusCardDriver.CloseDevice(m_nDeviceHandle);
		} else {
			AddLog("Open Device False!");
		}

	}

	public void OnClearLogListener(View arg0) {
		if (null == m_edtLog)
			return;
		m_edtLog.setText("");

	}

	public void OnAutoTestListener(View arg0) {
		if (-1 == m_nDeviceHandle) {
			m_nDeviceHandle = mLotusCardDriver.OpenDevice("", 0, 0,
					m_chkUseUsbHostApi.isChecked());
		}
		if (null == m_CardOperateThread) {
			m_CardOperateThread = new CardOperateThread();
		}
		if ((m_nDeviceHandle != -1) && (null != m_CardOperateThread)) {
			if (false == m_bCardOperateThreadRunning) {
				m_CardOperateThread.start();
				m_btnAutoTest.setText("AutoTesting");
				m_tvMessage.setText("Message:Please Put the IC Card to Reader");
			} else {
				m_CardOperateThread.cancel();
				m_btnAutoTest.setText("AutoTest");
				m_tvMessage.setText("Message:");
			}
			m_bCardOperateThreadRunning = !m_bCardOperateThreadRunning;

		}
	}

	private void testIcCardReader(int nDeviceHandle) {
		boolean bResult = false;
		int nRequestType;
		long lCardNo = 0;
		LotusCardParam tLotusCardParam1 = new LotusCardParam();
		bResult = mLotusCardDriver.Beep(nDeviceHandle, 10);
		// bResult = mLotusCardDriver.Beep(nDeviceHandle, 10);
		if (!bResult) {
			AddLog("Call Beep Error!");
			return;
		}
		AddLog("Call Beep Ok!");
		nRequestType = LotusCardDriver.RT_NOT_HALT;
		// 以下3个函数可以用GetCardNo替代
		// bResult = mLotusCardDriver.Request(nDeviceHandle, nRequestType,
		// tLotusCardParam1);
		// if (!bResult)
		// return;
		// bResult = mLotusCardDriver.Anticoll(nDeviceHandle, tLotusCardParam1);
		// if (!bResult)
		// return;
		// bResult = mLotusCardDriver.Select(nDeviceHandle, tLotusCardParam1);
		// if (!bResult)
		// return;
		bResult = mLotusCardDriver.GetCardNo(nDeviceHandle, nRequestType,
				tLotusCardParam1);
		if (!bResult) {
			AddLog("Call GetCardNo Error!");
			return;
		}
		lCardNo = bytes2long(tLotusCardParam1.arrCardNo);
		AddLog("Call GetCardNo Ok!");
		AddLog("CardNo(DEC):" + lCardNo);
		AddLog("CardNo(HEX):"
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrCardNo[3]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrCardNo[2]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrCardNo[1]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrCardNo[0]), 2)
						.toUpperCase());
		tLotusCardParam1.arrKeys[0] = (byte) 0xff;
		tLotusCardParam1.arrKeys[1] = (byte) 0xff;
		tLotusCardParam1.arrKeys[2] = (byte) 0xff;
		tLotusCardParam1.arrKeys[3] = (byte) 0xff;
		tLotusCardParam1.arrKeys[4] = (byte) 0xff;
		tLotusCardParam1.arrKeys[5] = (byte) 0xff;
		tLotusCardParam1.nKeysSize = 6;
		bResult = mLotusCardDriver.LoadKey(nDeviceHandle, LotusCardDriver.AM_A,
				0, tLotusCardParam1);
		if (!bResult) {
			AddLog("Call LoadKey Error!");
			return;
		}
		AddLog("Call LoadKey Ok!");
		bResult = mLotusCardDriver.Authentication(nDeviceHandle,
				LotusCardDriver.AM_A, 0, tLotusCardParam1);
		if (!bResult) {
			AddLog("Call Authentication(A) Error!");
			return;
		}
		AddLog("Call Authentication(A) Ok!");
		bResult = mLotusCardDriver.Read(nDeviceHandle, 1, tLotusCardParam1);
		if (!bResult) {
			AddLog("Call Read Error!");
			return;
		}
		AddLog("Call Read Ok!");
		AddLog("Buffer(HEX):"
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[1]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[2]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[3]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[4]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[5]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[6]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[7]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[8]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[9]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xa]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xb]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xc]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xd]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xe]), 2)
						.toUpperCase()
				+ leftString(
						Integer.toHexString(tLotusCardParam1.arrBuffer[0xf]), 2)
						.toUpperCase());
		tLotusCardParam1.arrBuffer[0] = (byte) 0x10;
		tLotusCardParam1.arrBuffer[1] = (byte) 0x01;
		tLotusCardParam1.arrBuffer[2] = (byte) 0x02;
		tLotusCardParam1.arrBuffer[3] = (byte) 0x03;
		tLotusCardParam1.arrBuffer[4] = (byte) 0x04;
		tLotusCardParam1.arrBuffer[5] = (byte) 0x05;
		tLotusCardParam1.arrBuffer[6] = (byte) 0x06;
		tLotusCardParam1.arrBuffer[7] = (byte) 0x07;
		tLotusCardParam1.arrBuffer[8] = (byte) 0x08;
		tLotusCardParam1.arrBuffer[9] = (byte) 0x09;
		tLotusCardParam1.arrBuffer[10] = (byte) 0x0a;
		tLotusCardParam1.arrBuffer[11] = (byte) 0x0b;
		tLotusCardParam1.arrBuffer[12] = (byte) 0x0c;
		tLotusCardParam1.arrBuffer[13] = (byte) 0x0d;
		tLotusCardParam1.arrBuffer[14] = (byte) 0x0e;
		tLotusCardParam1.arrBuffer[15] = (byte) 0x0f;
		tLotusCardParam1.nBufferSize = 16;
		bResult = mLotusCardDriver.Write(nDeviceHandle, 1, tLotusCardParam1);
		if (!bResult) {
			AddLog("Call Write Error!");
			return;
		}
		AddLog("Call Write Ok!");
	}

	private Boolean SetUsbCallBack() {
		Boolean bResult = false;
		PendingIntent pendingIntent;
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		m_UsbManager = (UsbManager) getSystemService(USB_SERVICE);
		if (null == m_UsbManager)
			return bResult;

		HashMap<String, UsbDevice> deviceList = m_UsbManager.getDeviceList();
		if (!deviceList.isEmpty()) {
			for (UsbDevice device : deviceList.values()) {
				if ((m_nVID == device.getVendorId())
						&& (m_nPID == device.getProductId())) {
					m_LotusCardDevice = device;
					m_strDeviceNode = m_LotusCardDevice.getDeviceName();
					break;
				}
			}
		}
		if (null == m_LotusCardDevice)
			return bResult;
		m_LotusCardInterface = m_LotusCardDevice.getInterface(0);
		if (null == m_LotusCardInterface)
			return bResult;
		if (false == m_UsbManager.hasPermission(m_LotusCardDevice)) {
			m_UsbManager.requestPermission(m_LotusCardDevice, pendingIntent);
		}
		UsbDeviceConnection conn = null;
		if (m_UsbManager.hasPermission(m_LotusCardDevice)) {
			conn = m_UsbManager.openDevice(m_LotusCardDevice);
		}

		if (null == conn)
			return bResult;

		if (conn.claimInterface(m_LotusCardInterface, true)) {
			m_LotusCardDeviceConnection = conn;
		} else {
			conn.close();
		}
		if (null == m_LotusCardDeviceConnection)
			return bResult;
		// 把上面获取的对性设置到接口中用于回调操作
		LotusCardDriver.m_UsbDeviceConnection = m_LotusCardDeviceConnection;
		if (m_LotusCardInterface.getEndpoint(1) != null) {
			LotusCardDriver.m_OutEndpoint = m_LotusCardInterface.getEndpoint(1);
		}
		if (m_LotusCardInterface.getEndpoint(0) != null) {
			LotusCardDriver.m_InEndpoint = m_LotusCardInterface.getEndpoint(0);
		}
		bResult = true;
		return bResult;
	}

	private void AddLog(String strLog) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String strDate = formatter.format(curDate);
		if (null == m_edtLog)
			return;
		String strLogs = m_edtLog.getText().toString().trim() ;
		if(strLogs.equals(""))
		{
			strLogs = strDate + " " + strLog;
		}
		else
		{
			strLogs += "\r\n" + strDate + " " + strLog;
		}
		m_edtLog.setText(strLogs);
	}

	public long bytes2long(byte[] byteNum) {
		long num = 0;
		for (int ix = 3; ix >= 0; --ix) {
			num <<= 8;
			if (byteNum[ix] < 0) {
				num |= (256 + (byteNum[ix]) & 0xff);
			} else {
				num |= (byteNum[ix] & 0xff);
			}
		}
		return num;
	}

	public String leftString(String strText, int nLeftLength) {
		if (strText.length() <= nLeftLength)
			return strText;

		return strText.substring(strText.length() - nLeftLength,
				strText.length());
	}

	public class CardOperateThread extends Thread {
		volatile boolean m_bStop = false;
		public void cancel() {
			Thread.currentThread().interrupt();
			m_bStop = true; 
		}

		public void run() {
			boolean bResult = false;
			int nRequestType;
			int nCount = 0;
			long lCardNo = 0;
			LotusCardParam tLotusCardParam1 = new LotusCardParam();

			while (!Thread.currentThread().isInterrupted()) {
				if(m_bStop) break;
				try {

					nRequestType = LotusCardDriver.RT_NOT_HALT;
					bResult = mLotusCardDriver.GetCardNo(m_nDeviceHandle,
							nRequestType, tLotusCardParam1);

					if (!bResult) {
						Thread.sleep(200);
						continue;
					}
					Message msg = new Message();
					lCardNo = bytes2long(tLotusCardParam1.arrCardNo);
					msg.obj = "CardNo(DEC):" + lCardNo;
					m_Handler.sendMessage(msg);
					Message msg1 = new Message();
					msg1.obj = "CardNo(HEX):"
							+ leftString(
									Integer.toHexString(tLotusCardParam1.arrCardNo[3]), 2)
									.toUpperCase()
							+ leftString(
									Integer.toHexString(tLotusCardParam1.arrCardNo[2]), 2)
									.toUpperCase()
							+ leftString(
									Integer.toHexString(tLotusCardParam1.arrCardNo[1]), 2)
									.toUpperCase()
							+ leftString(
									Integer.toHexString(tLotusCardParam1.arrCardNo[0]), 2)
									.toUpperCase();
					m_Handler.sendMessage(msg1);
					mLotusCardDriver.Beep(m_nDeviceHandle, 10);
					mLotusCardDriver.Halt(m_nDeviceHandle);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}