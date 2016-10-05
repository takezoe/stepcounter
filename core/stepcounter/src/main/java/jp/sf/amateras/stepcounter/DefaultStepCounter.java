package jp.sf.amateras.stepcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.sf.amateras.stepcounter.diffcount.Cutter;
import jp.sf.amateras.stepcounter.diffcount.DiffCounterUtil;
import jp.sf.amateras.stepcounter.diffcount.DiffSource;


/** カスタマイズして使用できる標準のステップカウンタです */
public class DefaultStepCounter implements StepCounter, Cutter {

	private static Pattern CATEGORY_PATTERN = Pattern.compile("\\[\\[(.*?)\\]\\]");
	private static Pattern IGNORE_PATTERN = Pattern.compile("\\[\\[IGNORE\\]\\]");

	private List<String> lineComments = new ArrayList<String>();
	private List<AreaComment> areaComments = new ArrayList<AreaComment>();
	private List<String> skipPatterns = new ArrayList<String>();
	private String fileType = "UNDEF";

	/**
	 * スキップするパターン（正規表現）を追加します。
	 *
	 * @param pattern スキップするパターン（正規表現）
	 */
	public void addSkipPattern(String pattern){
		this.skipPatterns.add(pattern);
	}

	/**
	 * スキップするパターンを取得します。
	 *
	 * @return スキップするパターン（正規表現）の配列
	 */
	public String[] getSkipPatterns(){
		return (String[])skipPatterns.toArray(new String[skipPatterns.size()]);
	}

	/** ファイルの種類を設定します */
	public void setFileType(String fileType){
		this.fileType = fileType;
	}

	/** ファイルの種類を取得します */
	public String getFileType(){
		return this.fileType;
	}

	/** 単一行コメントの開始文字列を追加します */
	public void addLineComment(String str){
		this.lineComments.add(str);
	}

	/** 複数行コメントを追加します */
	public void addAreaComment(AreaComment area){
		this.areaComments.add(area);
	}

	/** カウントします */
	public CountResult count(File file, String charset) throws IOException {
		String charSetName = charset;
		if (charSetName == null) {
			// キャラクタセット無指定の場合は
			// プラットフォームデフォルトキャラクタセットを指定します。
			charSetName = Charset.defaultCharset().name();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), charSetName));
		
		String category = "";
		long step    = 0;
		long non     = 0;
		long comment = 0;

		try {
			String line = null;
			boolean areaFlag = false;
			AreaComment lastAreaComment = new AreaComment();
	
			while((line = reader.readLine()) != null){
				if(category.length() == 0){
					Matcher matcher = CATEGORY_PATTERN.matcher(line);
					if(matcher.find()){
						category = matcher.group(1);
					}
				}
				if(IGNORE_PATTERN.matcher(line).find()){
					return null;
				}
	
				String trimedLine = line.trim();
				if(areaFlag==false){
					if(nonCheck(trimedLine)){
						non++;
					} else if(lineCommentCheck(trimedLine)){
						comment++;
					} else if(skipPatternCheck(trimedLine)){
						non++;
					} else if((lastAreaComment = areaCommentStartCheck(line))!=null){
						comment++;
						areaFlag = true;
					} else {
						step++;
					}
				} else {
					comment++;
					if(areaCommentEndCheck(line, lastAreaComment)){
						areaFlag = false;
					}
				}
			}
		} finally {
			reader.close();
		}
		return new CountResult(file, file.getName(), getFileType(), category, step, non, comment);
	}

	/** スキップパターンにマッチするかチェック */
	private boolean skipPatternCheck(String line){
		for(int i=0;i<skipPatterns.size();i++){
			if(Pattern.matches((String) skipPatterns.get(i), line)){
				return true;
			}
		}
		return false;
	}

	/** 空行かどうかをチェック */
	private boolean nonCheck(String line){
		if(line.equals("")){
			return true;
		}
		return false;
	}

	/** 単一行コメントかどうかをチェック */
	private boolean lineCommentCheck(String line){
		for(int i=0;i<lineComments.size();i++){
			if(line.startsWith((String) lineComments.get(i))){
				return true;
			}
		}
		for(int i=0;i<areaComments.size();i++){
			AreaComment area = (AreaComment) areaComments.get(i);
			String start = area.getStartString();
			String end   = area.getEndString();

			int startIndex = line.indexOf(start);
			int endIndex = line.indexOf(end,startIndex);
			if(startIndex==0) { 
				if(endIndex==line.length()-end.length()) {
					return true;
				}
				String commentRemovedLine = line.substring(0, endIndex + end.length());
				return lineCommentCheck(commentRemovedLine.trim());
			}
		}
		return false;
	}

	/**
	 * 複数行コメントが開始しているかチェック
	 */
	private AreaComment areaCommentStartCheck(String line){
		for(int i=0;i<areaComments.size();i++){
			AreaComment area = (AreaComment) areaComments.get(i);
			String start = area.getStartString();
			String end   = area.getEndString();

			int index = line.indexOf(start);
			if(index>=0 && line.indexOf(end,index)<0){
				return area;
			}
		}
		return null;
	}

	/** 複数行コメントが終了しているかチェック */
	private boolean areaCommentEndCheck(String line,AreaComment area){
		String end = area.getEndString();
		if(line.indexOf(end)>=0){
			return true;
		}
		return false;
	}

	public DiffSource cut(String source) {
		String line = null;
		String category = "";
		boolean isIgnore = false;
		BufferedReader reader = new BufferedReader(new StringReader(source));

		try {
			while((line = reader.readLine()) != null){
				if(category.length() == 0){
					Matcher matcher = CATEGORY_PATTERN.matcher(line);
					if(matcher.find()){
						category = matcher.group(1);
					}
				}
				if(IGNORE_PATTERN.matcher(line).find()){
					isIgnore = true;
				}
			}
		} catch(IOException ex){
			ex.printStackTrace();
		} finally {
			Util.close(reader);
		}

		// 複数行コメントを削除
		for(AreaComment areaComment: this.areaComments){
			Pattern	pattern = Pattern.compile(
					Pattern.quote(areaComment.getStartString()) + ".*?" + Pattern.quote(areaComment.getEndString()),
					Pattern.DOTALL);
			Matcher matcher = pattern.matcher(source);
			source = matcher.replaceAll("");
		}

		// 単一コメントを削除
		for(String lineComment: this.lineComments){
			Pattern	pattern = Pattern.compile(Pattern.quote(lineComment) + ".*");
			Matcher matcher = pattern.matcher(source);
			source = matcher.replaceAll("");
		}

		// 空行を削除して返却
		return new DiffSource(DiffCounterUtil.removeEmptyLines(source), isIgnore, category);
	}

//    /**
//     * 文字列中の任意の文字列を指定した文字列に置換します。
//     *
//     * @param s 変換対象の文字列。
//     * @param s1 s2に置き換わる文字列。
//     * @param s2 s1に置き換える文字列。
//     * @return 変換後の文字列。sがnullの場合は空文字列を返します。
//     */
//    private static String replace(String s,String s1,String s2){
//
//        // s がNULLだった場合、空文字列を返す
//        if(s==null){ return ""; }
//
//        StringBuffer sb = new StringBuffer();
//        for(int i=0;i<s.length();i++){
//            if(s.indexOf(s1,i)==i){
//                sb.append(s2);
//                i = i + s1.length() - 1;
//            } else {
//                sb.append(s.charAt(i));
//            }
//        }
//        return sb.toString();
//    }
}