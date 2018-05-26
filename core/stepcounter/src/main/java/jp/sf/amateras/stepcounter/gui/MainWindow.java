package jp.sf.amateras.stepcounter.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import jp.sf.amateras.stepcounter.*;
import jp.sf.amateras.stepcounter.format.*;

/** ステップカウンタのメインウィンドウ */
@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class MainWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 5954076353117692515L;

	private String fontName = "ＭＳ ゴシック";
	private int    fontSize = 12;

	private JRadioButton radioDirectory = new JRadioButton("Directory", false);
	private JRadioButton radioFile = new JRadioButton("File", true);

	private JButton buttonExecute = new JButton("Execute");
	private JComboBox comboFormat = new JComboBox();
	private JTextArea textArea    = new JTextArea();

	private DefaultComboBoxModel model = new DefaultComboBoxModel();
	private JList list = new JList(model);

	private JButton buttonAdd    = new JButton("Add");
	private JButton buttonRemove = new JButton("Remove");

	private ButtonGroup buttonGroup = new ButtonGroup();

	//private OutputThread thread = new OutputThread(this);
	private ConfigManager config = new ConfigManager(new File("stepcounter.conf"));

	private File lastDirectory = null;

//	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	/** コンストラクタ */
	public MainWindow(){
		loadConfig();
		initComponents();
		setSize(600,450);
	}

	/** コンポーネントの初期化 */
	private void initComponents(){

		setTitle("STEP COUNTER");
		this.addWindowListener(new MainWindowListener(this));

		buttonExecute.addActionListener(this);
		buttonAdd.addActionListener(this);
		buttonRemove.addActionListener(this);

		comboFormat.addItem("Default");
		comboFormat.addItem("CSV");

		buttonGroup.add(radioDirectory);
		buttonGroup.add(radioFile);

		JScrollPane scrText = new JScrollPane(textArea);
		JScrollPane scrList = new JScrollPane(list);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// 上部（ファイル一覧）を配置
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(scrList,BorderLayout.CENTER);
		panel1.add(new JLabel("Search Files"),BorderLayout.NORTH);
		// ファイルの追加、削除ボタンを配置
		JPanel panel3 = new JPanel();
		panel3.add(buttonAdd);
		panel3.add(buttonRemove);

		// ディレクトリと単体ファイルで出力形式を選択するラジオボタンを配置
		JPanel panel6 = new JPanel();
		panel6.add(radioDirectory);
		panel6.add(radioFile);

		// フォーマット選択コンボと実行ボタンを配置
		JPanel panel5 = new JPanel();
		panel5.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel5.add(new JLabel("Format"));
		panel5.add(comboFormat);
		panel5.add(buttonExecute);
		// 中間部分（ボタン類）を配置
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(panel3,BorderLayout.WEST);
		panel2.add(panel6,BorderLayout.CENTER);
		panel2.add(panel5,BorderLayout.EAST);
		// 中間部分と下部（結果出力エリア）を配置
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BorderLayout());
		panel4.add(panel2,BorderLayout.NORTH);
		panel4.add(scrText,BorderLayout.CENTER);
		// 全体を配置
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(panel4,BorderLayout.CENTER);
		cp.add(panel1,BorderLayout.NORTH);

		textArea.setFont(new Font(this.fontName,Font.PLAIN,this.fontSize));
	}

	/**
	 * テキストエリアにテキストを追加します。
	 */
	public void appendText(String text){
		textArea.append(text);
		textArea.setCaretPosition(textArea.getText().length());
	}

	/**
	 * テキストエリアをクリアします。
	 */
	public void clearText(){
		textArea.setText("");
	}

	/**
	 * 設定を保存します。終了時に呼び出されます。
	 */
	public void saveConfig(){
		try {
			String[] files = new String[model.getSize()];
			for(int i=0;i<model.getSize();i++){
				files[i] = ((File)model.getElementAt(i)).toString();
			}
			config.setProperty("Path",files);
			config.setProperty("FontName",this.fontName);
			config.setProperty("FontSize",String.valueOf(this.fontSize));
			config.save();
		} catch(IOException ioEx){ }
	}

	/**
	 * 設定を読み込みます。起動時に呼び出されます。
	 */
	public void loadConfig(){
		try {
			String[] files = config.getPropertyValues("Path");
			for(int i=0;i<files.length;i++){
				model.addElement(new File(files[i]));
			}
			String fontName = config.getProperty("FontName");
			if(fontName!=null){
				this.fontName = fontName;
			}
			String fontSize = config.getProperty("FontSize");
			if(fontSize!=null){
				this.fontSize = Integer.parseInt(fontSize);
			}
		} catch(Exception ioEx){ }
	}


	/** アクションイベントのイベントハンドラ */
	public void actionPerformed(ActionEvent evt){
		Object obj = evt.getSource();
		if(obj == buttonExecute){ // 実行
			File[] files = new File[model.getSize()];
			for(int i=0;i<model.getSize();i++){
				files[i] = (File)model.getElementAt(i);
			}
			String format = (String)comboFormat.getSelectedItem();
			boolean showDirectory = radioDirectory.isSelected();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(out));
			Main counter = new Main();
			counter.setFiles(files);
			counter.setFormatter(FormatterFactory.getFormatter(format));
			counter.setShowDirectory(showDirectory);
			try {
				counter.executeCount();
				byte[] buf = out.toByteArray();
				if(buf!=null && buf.length>0){
					appendText(StringUtility.replace(new String(buf,0,buf.length),"\r\n","\n"));
				}
				out.close();
			} catch(IOException ioEx){
				ioEx.printStackTrace();
			}
		} else if(obj == buttonAdd){ // 追加
			JFileChooser chooser = new JFileChooser(lastDirectory);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setMultiSelectionEnabled(true);
			if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
				File[] files = chooser.getSelectedFiles();
				if(files!=null && files.length>0){
					for(int i=0;i<files.length;i++){
						model.addElement(files[i]);
					}
					lastDirectory = files[0].getParentFile();
				}
			}
		} else if(obj == buttonRemove){ // 削除
			Object[] files = list.getSelectedValues();
			if(files!=null){
				for(int i=0;i<files.length;i++){
					model.removeElement(files[i]);
				}
			}
		}
	}

	/**
	 * 起動メソッド<BR>
	 * java -cp stepcounter.jar tk.stepcounter.gui.MainWindow
	 */
	public static void main(String[] args){
		MainWindow window = new MainWindow();
		window.setVisible(true);
	}
}