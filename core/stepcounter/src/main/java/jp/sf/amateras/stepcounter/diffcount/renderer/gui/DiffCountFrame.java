package jp.sf.amateras.stepcounter.diffcount.renderer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.GroupLayout; // <-- javax.swing.GroupLayout へ移行
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.sf.amateras.stepcounter.diffcount.DiffCounter;
import jp.sf.amateras.stepcounter.diffcount.object.AbstractDiffResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

// VS4E -- DO NOT REMOVE THIS LINE!
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

        // 先にコンポーネントを用意
        JLabel lblBefore = getJLabel0();
        JTextField tfBefore = getJTextField0();
        JButton btnBefore = getBrowseOldRoot();

        JLabel lblAfter = getJLabel1();
        JTextField tfAfter = getJTextField1();
        JButton btnAfter = getBrowseNewRoot();

        JButton btnExec = getExecuteDiffCount();
        JScrollPane scroll = getJScrollPane0();

        // javax.swing.GroupLayout でレイアウトを定義
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // 水平方向のグループ
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                // 1 行目: [ラベル][テキストフィールド 伸長][参照ボタン]
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblBefore)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tfBefore)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnBefore))
                // 2 行目: [ラベル][テキストフィールド 伸長][参照ボタン]
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblAfter)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tfAfter)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnAfter))
                // 3 行目: 実行ボタンを右寄せ
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnExec))
                // 4 行目: スクロールペインを全幅に伸長
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // 垂直方向のグループ
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblBefore)
                    .addComponent(tfBefore, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBefore))
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblAfter)
                    .addComponent(tfAfter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAfter))
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(btnExec))
                // スクロールペインは残りの高さを占有
                .addComponent(scroll, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // 旧 VS4E の setSize(475, 258) 近辺に合わせた推奨サイズ
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
                public void removeUpdate(DocumentEvent e) { updateStatus(); }
                public void insertUpdate(DocumentEvent e) { updateStatus(); }
                public void changedUpdate(DocumentEvent e) { updateStatus(); }
            });
        }
        return newRoot;
    }

    private JTextField getJTextField0() {
        if (oldRoot == null) {
            oldRoot = new JTextField();
            oldRoot.getDocument().addDocumentListener(new DocumentListener() {
                public void removeUpdate(DocumentEvent e) { updateStatus(); }
                public void insertUpdate(DocumentEvent e) { updateStatus(); }
                public void changedUpdate(DocumentEvent e) { updateStatus(); }
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
     * 
     * @param args Command line args of main static method.
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
