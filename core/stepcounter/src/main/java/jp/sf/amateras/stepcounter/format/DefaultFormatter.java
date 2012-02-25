package jp.sf.amateras.stepcounter.format;

import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.Util;

/**
 * �J�E���g���ʂ��t�H�[�}�b�g���ďo�͂���N���X�ł��B
 *
 * TODO ���ۉ�
 */
public class DefaultFormatter implements ResultFormatter {

	public byte[] format(CountResult[] results){

		long sumStep    = 0;
		long sumComment = 0;
		long sumNone    = 0;

		int maxFileLength = getFileLength(results);
		// �w�b�_���t�H�[�}�b�g
		StringBuffer sb = new StringBuffer();
		sb.append(fillOrCut("�t�@�C��", maxFileLength));
		sb.append("���  �J�e�S��            ���s  ��s  ����  ���v  ");
		sb.append("\n");
		sb.append(makeHyphen(maxFileLength));
		sb.append("--------------------------------------------------");
		sb.append("\n");
		// �P�s���������s��
		for(int i=0;i<results.length;i++){
			CountResult result = results[i];
			// ���Ή��̃J�E���g���ʂ��t�H�[�}�b�g
			if(result.getFileType()==null){
				sb.append(fillOrCut(result.getFileName(), maxFileLength));
				sb.append("���Ή�");
				sb.append("\n");
			// ����ɃJ�E���g���ꂽ���ʂ��t�H�[�}�b�g
			} else {
//				String fileName = result.getFileName();
//				String fileType = result.getFileType();
				String step     = String.valueOf(result.getStep());
				String non      = String.valueOf(result.getNon());
				String comment  = String.valueOf(result.getComment());
				String sum      = String.valueOf(result.getStep() + result.getNon() + result.getComment());

				sb.append(fillOrCut(result.getFileName(), maxFileLength));
				sb.append(fillOrCut(result.getFileType(), 6));
				sb.append(fillOrCut(result.getCategory(),20));
				sb.append(leftFillOrCut(step    , 6));
				sb.append(leftFillOrCut(non     , 6));
				sb.append(leftFillOrCut(comment , 6));
				sb.append(leftFillOrCut(sum     , 6));
				sb.append("\n");

				sumStep    += result.getStep();
				sumComment += result.getComment();
				sumNone    += result.getNon();
			}
		}
		// ���v�s���t�H�[�}�b�g
		sb.append(makeHyphen(maxFileLength));
		sb.append("--------------------------------------------------");
		sb.append("\n");
		sb.append(fillOrCut("���v", maxFileLength));
		sb.append(makeSpace(6));
		sb.append(makeSpace(20));
		sb.append(leftFillOrCut(String.valueOf(sumStep)   ,6));
		sb.append(leftFillOrCut(String.valueOf(sumNone)   ,6));
		sb.append(leftFillOrCut(String.valueOf(sumComment),6));
		sb.append(leftFillOrCut(String.valueOf(sumStep + sumNone + sumComment),6));
		sb.append("\n");

		return sb.toString().getBytes();
	}

	/** �t�@�C�����̒����ɍ��킹���ő咷���擾���܂��i�ŏ�40�j */
	protected int getFileLength(CountResult[] results) {
		int fileLength = 40;
		if (results == null || results.length == 0) {
			return fileLength;
		}
		for (CountResult result : results) {
			String fileName = result.getFileName();
			
			if (fileName != null) {
				int len = getDisplayWidth(fileName);
				if (fileLength < len) fileLength = len;
			}
		}
		return fileLength;
	}
	
	/** �e�L�X�g�̕\�������v�Z���܂� */
	private int getDisplayWidth(String str) {
		int len = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			// ASCII�E���[���b�p��������� HALFWIDTH �̂ݔ��p�Ɣ��f
			if (c <= 0x00FF || (c >= 0xFF61 && c <= 0xFFDC) || (c >= 0xFFE8 && c <= 0xFFEE)) {
				len += 1;
			} else {
				len += 2;
			}
		}
		return len;
	}
	
	/** �w�肳�ꂽ�����̔��p�X�y�[�X���쐬���܂� */
	private String makeSpace(int width){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<width;i++){
			sb.append(" ");
		}
		return sb.toString();
	}

	/** �w�肳�ꂽ�����̔��p�n�C�t�����쐬���܂� */
	private String makeHyphen(int width){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<width;i++){
			sb.append('-');
		}
		return sb.toString();
	}

	/**
	 * �����񂪎w��̒��������ł���ΉE�����X�y�[�X�Ŗ��߁A
	 * �w��̒����ȏ�ł���ΉE����؂藎�Ƃ��܂��B
	 */
	private String fillOrCut(String str,int width){
		int length = getDisplayWidth(str);
		if(length==width){
			return str;
		} else if(length < width){
			return str + makeSpace(width - length);
		} else {
			return Util.substring(str,width);
		}
	}

	/**
	 * �����񂪎w��̒��������ł���΍������X�y�[�X�Ŗ��߁A
	 * �w��̒����ȏ�ł���ΉE����؂藎�Ƃ��܂��B
	 */
	private String leftFillOrCut(String str,int width){
		int length = Util.getByteLength(str);
		if(length==width){
			return str;
		} else if(length < width){
			return makeSpace(width - length) + str;
		} else {
			return Util.substring(str,width);
		}
	}
}