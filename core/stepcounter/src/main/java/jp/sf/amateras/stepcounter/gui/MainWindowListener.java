package jp.sf.amateras.stepcounter.gui;

import java.awt.event.*;

/**
 * メインウィンドウのイベントハンドラ。<br>
 * ウィンドウが閉じられた際にプロセスを終了します。
 */
public class MainWindowListener extends WindowAdapter {
	
	private MainWindow window;
	
	/**
	 * コンストラクタ
	 * 
	 * @param window メインウィンドウ。
	 */
	public MainWindowListener(MainWindow window){
		this.window = window;
	}
	
	/**
	 * ウィンドウが閉じられたときのイベントハンドラ
	 */
	public void windowClosing(WindowEvent e){
		this.window.saveConfig();
		System.exit(0);
	}

}