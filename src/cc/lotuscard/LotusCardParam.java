package cc.lotuscard;

public class LotusCardParam {
	LotusCardParam()
	{
		arrCardNo = new byte[4];
		arrBuffer = new byte[64];
		arrKeys = new byte[64];
	}
	/**
	 * ��Ƭ����
	 */
	public int nCardType;
	/**
	 * 4�ֽڿ���
	 */
	public byte[] arrCardNo;
	
	/**
	 * ��Ƭ������С
	 */
	public int nCardSize;
	
	/**
	 * ��д����
	 */
	public byte[] arrBuffer;
	
	/**
	 * �����С
	 */
	public int nBufferSize;
	/**
	 * ��Կ
	 */
	public byte[] arrKeys;
	
	/**
	 * KEYs��С
	 *
	 */
	public int nKeysSize;
}
