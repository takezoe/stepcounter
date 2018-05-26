package jp.sf.amateras.stepcounter.diffcount.renderer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.sf.amateras.stepcounter.diffcount.DiffCounter;
import jp.sf.amateras.stepcounter.diffcount.object.AbstractDiffResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;


//VS4E -- DO NOT REMOVE THIS LINE!
public class DiffCountFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel0;
	private JButton browseOldRoot;
	private JLabel jLabel1;
	private JButton browseNewRoot;
	private JTextField oldRoot;
	private JTextField newRoot;

	private JButton executeDiffCount;
	private JXTreeTable resultTable;
	private JScrollPane jScrollPane0;
	private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public DiffCountFrame() {
		initComponents();
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new GroupLayout());
		add(getJLabel0(), new Constraints(new Leading(12, 12, 12), new Leading(12, 12, 12)));
		add(getJTextField0(), new Constraints(new Bilateral(123, 82, 4), new Leading(10, 12, 12)));
		add(getBrowseOldRoot(), new Constraints(new Trailing(12, 129, 129), new Leading(8, 12, 12)));
		add(getJLabel1(), new Constraints(new Leading(12, 100, 100), new Leading(38, 12, 12)));
		add(getJTextField1(), new Constraints(new Bilateral(123, 82, 6), new Leading(35, 12, 12)));
		add(getBrowseNewRoot(), new Constraints(new Trailing(12, 131, 131), new Leading(34, 12, 12)));
		add(getExecuteDiffCount(), new Constraints(new Trailing(12, 12, 12), new Leading(63, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Bilateral(12, 12, 23), new Bilateral(90, 12, 23)));
		setSize(475, 258);
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJTable0());
		}
		return jScrollPane0;
	}

	private JXTreeTable getJTable0() {
		if (resultTable == null) {
			resultTable = new JXTreeTable(new DiffCountTreeTableModel());
			resultTable.setEditable(false);
		}
		return resultTable;
	}

	private JButton getExecuteDiffCount() {
		if (executeDiffCount == null) {
			executeDiffCount = new JButton();
			executeDiffCount.setText("ステップ数の差分を測定");
			executeDiffCount.setEnabled(false);
			executeDiffCount.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					executeDiffCountActionActionPerformed(event);
				}
			});
		}
		return executeDiffCount;
	}

	private JButton getBrowseNewRoot() {
		if (browseNewRoot == null) {
			browseNewRoot = new JButton();
			browseNewRoot.setText("参照...");
			browseNewRoot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					browseNewRootActionActionPerformed(event);
				}
			});
		}
		return browseNewRoot;
	}

	private JButton getBrowseOldRoot() {
		if (browseOldRoot == null) {
			browseOldRoot = new JButton();
			browseOldRoot.setText("参照...");
			browseOldRoot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					browseOldRootActionActionPerformed(event);
				}
			});
		}
		return browseOldRoot;
	}

	private JTextField getJTextField1() {
		if (newRoot == null) {
			newRoot = new JTextField();
			newRoot.getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {
					updateStatus();
				}

				public void insertUpdate(DocumentEvent e) {
					updateStatus();
				}

				public void changedUpdate(DocumentEvent e) {
					updateStatus();
				}
			});
		}
		return newRoot;
	}

	private JTextField getJTextField0() {
		if (oldRoot == null) {
			oldRoot = new JTextField();
			oldRoot.getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {
					updateStatus();
				}

				public void insertUpdate(DocumentEvent e) {
					updateStatus();
				}

				public void changedUpdate(DocumentEvent e) {
					updateStatus();
				}
			});
		}
		return oldRoot;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("変更後のソースツリー:");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("変更前のソースツリー:");
		}
		return jLabel0;
	}

	private static void installLnF() {
		try {
			String lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DiffCountFrame frame = new DiffCountFrame();
				frame.setDefaultCloseOperation(DiffCountFrame.EXIT_ON_CLOSE);
				frame.setTitle("差分測定カウンタ");
				frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * 変更前のソースツリーの参照ボタンのクリック時に呼び出されるイベントハンドラです。
	 *
	 * @param event イベント
	 */
	private void browseOldRootActionActionPerformed(ActionEvent event) {
		File file = chooseFolder();
		if(file != null){
			oldRoot.setText(file.getAbsolutePath());
			updateStatus();
		}
	}

	/**
	 * 変更後のソースツリーの参照ボタンのクリック時に呼び出されるイベントハンドラです。
	 *
	 * @param event イベント
	 */
	private void browseNewRootActionActionPerformed(ActionEvent event) {
		File file = chooseFolder();
		if(file != null){
			newRoot.setText(file.getAbsolutePath());
			updateStatus();
		}
	}

	/**
	 * ディレクトリ選択ダイアログでディレクトリを選択します。
	 *
	 * @return 選択されたディレクトリ。キャンセルされた場合はnullを返します。
	 */
	private File chooseFolder(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * ステップ数の差分を測定ボタンの状態を更新します。
	 */
	private void updateStatus(){
		executeDiffCount.setEnabled(false);

		String oldRoot = this.oldRoot.getText();
		String newRoot = this.newRoot.getText();

		if(oldRoot.length() != 0 && newRoot.length() != 0){

			File oldFile = new File(oldRoot);
			File newFile = new File(newRoot);

			if(oldFile.exists() && oldFile.isDirectory() &&
					newFile.exists() && newFile.isDirectory()){
				executeDiffCount.setEnabled(true);
			}
		}
	}

	/**
	 * ステップ数の差分を測定ボタンのクリック時に呼び出されるイベントハンドラです。
	 * <p>
	 * ステップ数の差分を測定し、<code>JTable</code>に測定結果を表示します。
	 *
	 * @param event イベント
	 */
	private void executeDiffCountActionActionPerformed(ActionEvent event) {
		File oldRoot = new File(this.oldRoot.getText());
		File newRoot = new File(this.newRoot.getText());

		DiffFolderResult root = DiffCounter.count(oldRoot, newRoot);

        DiffCountTreeTableModel model = (DiffCountTreeTableModel) resultTable.getTreeTableModel();
        model.setRoot(wrapToTreeTableModel(root));
	}

	/**
	 * カウント結果をJXTreeTableで表示するためにDefaultMutableTreeTableNodeでラップします。
	 */
	private DefaultMutableTreeTableNode wrapToTreeTableModel(DiffFolderResult folder){

        DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(folder);

        for(AbstractDiffResult child: folder.getChildren()){
        	if(child instanceof DiffFolderResult){
        		node.add(wrapToTreeTableModel((DiffFolderResult) child));
        	} else {
                DefaultMutableTreeTableNode childNode = new DefaultMutableTreeTableNode(child);
        		node.add(childNode);
        	}
        }

        return node;
	}

}
