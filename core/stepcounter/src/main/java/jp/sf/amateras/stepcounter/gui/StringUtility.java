package jp.sf.amateras.stepcounter.gui;

import java.util.*;

/**
 * 文字列の操作を行う各種staticメソッドを提供するクラスです。
 *
 * @author  Naoki Takezoe
 * @version 2.0
 */
public class StringUtility {

	/*
	// 動作確認用起動メソッド
	public static void main(String[] args){


		String i="00";


		System.out.println("["+StringUtility.cut(i,3)+"]");
		System.out.println("[100]");
	}
	*/

	/**
	 * HTMLの特殊記号を実態参照に変換します。
	 * 改行コードも&lt;BR&gt;タグに変換します。
	 *
	 * @param str 変換対象の文字列
	 * @return 変換後の文字列
	 */
	public static String tagFilter(String str){

		str = StringUtility.replace(str,"<","&lt;");
		str = StringUtility.replace(str,">","&gt;");
		str = StringUtility.replace(str,"\"","&quot;");
		str = StringUtility.replace(str,"\r\n","<BR>"); // Win
		str = StringUtility.replace(str,"\r","<BR>");   // Mac
		str = StringUtility.replace(str,"\n","<BR>");   // Unix

		return str;
	}

	/**
	 * HTMLの特殊記号を実態参照に変換します。
	 * ただし改行コードは変換しません。
	 * フォームに属性値を格納する際などに使用してください。
	 *
	 * @param str 変換対象の文字列
	 * @return 変換後の文字列
	 */
	public static String formFilter(String str){

		str = StringUtility.replace(str,"<","&lt;");
		str = StringUtility.replace(str,">","&gt;");
		str = StringUtility.replace(str,"\"","&quot;");

		return str;
	}

	/**
	 * 文字列中の任意の文字列を指定した文字列に置換します。
	 *
	 * @param s 変換対象の文字列。
	 * @param s1 s2に置き換わる文字列。
	 * @param s2 s1に置き換える文字列。
	 * @return 変換後の文字列。sがnullの場合は空文字列を返します。
	 */
	public static String replace(String s,String s1,String s2){

		// s がNULLだった場合、空文字列を返す
		if(s==null){ return ""; }

		StringBuffer sb = new StringBuffer();
		for(int i=0;i<s.length();i++){
			if(s.indexOf(s1,i)==i){
				sb.append(s2);
				i = i + s1.length() - 1;
			} else {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 文字列が指定のバイト長よりも短かった場合に左側をスペースで埋めます。
	 * 指定のバイト長よりも長かった場合はなにもしません。
	 *
	 * @param str 変換対象の文字列
	 * @param length バイト長
	 * @return 変換後の文字列
	 */
	public static String fillLeftSpace(String str,int length){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length-StringUtility.getBytes(str);i++){
			sb.append(" ");
		}
		sb.append(str);
		return sb.toString();
	}

	/**
	 * 文字列が指定のバイト長よりも短かった場合に右側をスペースで埋めます。
	 * 指定のバイト長よりも長かった場合はなにもしません。
	 *
	 * @param str 変換対象の文字列
	 * @param length バイト長
	 * @return 変換後の文字列
	 */
	public static String fillRightSpace(String str,int length){

		int strLength = StringUtility.getBytes(str);
		if(strLength>=length){ return str; }

		String space = StringUtility.createSpace(length-strLength);

		return str + space;
	}

	/**
	 * 文字列が指定の文字数（バイト長ではない）を超える場合に、
	 * 超えた分をカットした文字列を返します。
	 * 指定の文字数より少ない場合は何もせずに返します。
	 *
	 * @param str 変換対象の文字列
	 * @param length 文字数
	 * @return 変換後の文字列
	 */
	public static String cut(String str,int length){
		if(str.length() <= length){ return str; }
		return str.substring(0,length);
	}

	/**
	 * 文字列がNULLだった場合に空文字列に変換します。
	 *
	 * @param s 変換対象の文字列。
	 * @return sがnullの場合は空文字列を、それ以外の場合は元の文字列を返します。
	 */
	public static String nullConvert(String s){
		if(s==null){ return ""; } else { return s; }
	}

	/**
	 * 文字列がNULLだった場合に指定した文字列に変換します。
	 *
	 * @param s 変換対象の文字列。
	 * @return sがnullの場合は指定した文字列を、それ以外の場合は元の文字列を返します。
	 */
	public static String nullConvert(String s1,String s2){
		if(s1==null){ return s1; } else { return s2; }
	}


	/**
	 * 文字列を指定した文字列で分割し、配列で返します。
	 *
	 * @param s1 分割対象の文字列。
	 * @param s2 分割時の区切りに使用する文字列。
	 * @return 分割結果を配列で返します。s1がnullの場合はnullを返します。
	 */
	public static String[] split(String s1,String s2){
		if(s1 == null){ return null; }
		Vector<String> v  = new Vector<String>();
		String sb = "";
		for(int i=0;i<s1.length();i++){
			if(s1.indexOf(s2,i)==i){
				v.add(sb.toString());
				i = i + s2.length() - 1;
				sb = "";
			} else {
				sb = sb + s1.charAt(i);
			}
		}
		if(sb.length()!=0){ v.add(sb); }

		String[] ret = new String[v.size()];
		for(int i=0;i<v.size();i++){
			ret[i] = (String)v.get(i);
		}
		return ret;
	}

	/**
	 * 文字列のバイト長を返すメソッドです。
	 *
	 * @param str 検査対象の文字列。
	 * @return 引数の文字列sのバイト長。
	 */
	public static int getBytes(String str){ return str.getBytes().length; }


	/**
	 * 文字列の先頭と末尾のタブ、スペースを排除します。引数がNULLの場合は空文字列を返します。
	 *
	 * @param str 文字列。
	 * @return 引数の文字列strからタブ、スペースを取り除いた文字列。
	 */
	public static String trim(String str){
		// NULLの場合、空文字列を返す
		if(str==null){ return ""; }

		StringBuffer sb = new StringBuffer();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(c!=' ' && c!='\t' && c!='　'){ sb.append(c); }
		}
		return sb.toString();
	}

	/**
	 * 文字列の末尾のタブ、スペースを排除します。引数がNULLの場合は空文字列を返します。
	 *
	 * @param str 文字列。
	 * @return 引数の文字列strの末尾からタブ、スペースを取り除いた文字列。
	 */
	public static String rTrim(String str){
		// NULLの場合、空文字列を返す
		if(str==null){ return ""; }
		char c = ' ';
		int index = str.length();
		while((c==' ' || c=='　' || c=='\t') && index > 0){
			index = index - 1;
			c = str.charAt(index);
		}
		return str.substring(0,index+1);
	}

	/**
	 * 文字列の先頭のタブ、スペースを排除します。引数がNULLの場合は空文字列を返します。
	 *
	 * @param str 文字列。
	 * @return 引数の文字列strの先頭からタブ、スペースを取り除いた文字列。
	 */
	public static String lTrim(String str){
		// NULLの場合、空文字列を返す
		if(str==null){ return ""; }
		char c = ' ';
		int index = -1;
		while((c==' ' || c=='　' || c=='\t') && index < str.length()){
			index = index + 1;
			c = str.charAt(index);
		}
		return str.substring(index,str.length());
	}

	/**
	 * スペースから成る指定の長さの文字列を作成します。
	 *
	 * @param num 作成するスペース文字列の長さ。
	 * @return スペースから成る文字列。
	 */
	public static String createSpace(int num){
		if(num <= 0){ return ""; }
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<num;i++){ sb.append(" "); }
		return sb.toString();
	}

	/**
	 * 文字列型配列の各要素を指定した境界文字列を使って連結します。
	 *
	 * @param str 連結する文字列型の１次元配列。
	 * @param del 連結時の境界文字列。
	 * @return 連結した文字列。
	 */
	public static String join(String[] str,String del){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<str.length;i++){
			sb.append(str[i]);
			if(i<str.length-1){ sb.append(del); }
		}
		return sb.toString();
	}

	/**
	 * 末尾の改行コードを切り落とした文字列を返します。
	 * @param str 文字列。
	 * @return 引数の文字列から末尾の改行コードを切り落とした文字列。
	 */
	public static String chomp(String str){
		if(str.endsWith("\r\n")){ return str.substring(0,str.length()-2); }
		if(str.endsWith("\r") || str.endsWith("\n")){ return str.substring(0,str.length()-1); }
		return str;
	}
}