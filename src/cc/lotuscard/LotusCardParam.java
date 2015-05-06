package cc.lotuscard;

public class LotusCardParam {
	LotusCardParam()
	{
		arrCardNo = new byte[4];
		arrBuffer = new byte[64];
		arrKeys = new byte[64];
	}
	/**
	 * 卡片类型
	 */
	public int nCardType;
	/**
	 * 4字节卡号
	 */
	public byte[] arrCardNo;
	
	/**
	 * 卡片容量大小
	 */
	public int nCardSize;
	
	/**
	 * 读写缓冲
	 */
	public byte[] arrBuffer;
	
	/**
	 * 缓冲大小
	 */
	public int nBufferSize;
	/**
	 * 密钥
	 */
	public byte[] arrKeys;
	
	/**
	 * KEYs大小
	 *
	 */
	public int nKeysSize;
}
