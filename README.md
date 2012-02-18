StepCounter
======================
様々なプログラミング言語に対応したステップカウンタです。GUIとCUIの両方をサポートしており、AntやEclipseにも対応しています。

使い方
----------------
### コマンドライン

コマンドラインで使用するにはstepcounter-x.x.x-jar-with-dependencies.jarをダウンロードし、コマンドラインから次のように入力してください。

    > java -cp stepcounter-x.x.x-jar-with-dependencies.jar tk.stepcounter.Main [ファイル名] [ファイル名] ...

結果は標準出力に出力されます。ファイル名にはフォルダを指定することも可能で、
その場合そのフォルダに含まれる全てのファイルがカウント対象となりますので、
例えばJavaで複数のパッケージ内のファイルを一括カウントしたい場合などはトップのフォルダだけ指定してやればOKです。

-format=csvというオプションを与えることでCSV形式、-format=excelというオプションを与えることでExcel形式で出力することも
可能です（Excel形式で出力する場合はlibディレクトリに格納されているJARファイル群にもクラスパスを通す必要があります）。

また、-output=ファイル名というオプションを与えることで標準出力ではなくファイルへ出力を行ないます。
CSV形式でcount.txtファイルへの出力を行なうには以下のようにします。

    > java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.Main -format=csv -output=count.txt -encoding=UTF-8 [ファイル名] [ファイル名] ...

差分カウンタの場合は以下のように使用します。指定可能なオプションは通常のステップカウンタの場合と同じです。

    > java -cp stepcounter-x.x.x-jar-with-dependencies.jar tk.stepcounter.diffcount.Main [新版のディレクトリ名] [旧版のディレクトリ名]

### Swingアプリケーション

Swingアプリケーション版を使用するにはコマンドラインから以下のように入力してください。

    > java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.gui.MainWindow

差分カウンタの場合は以下のようにして起動します。

    > java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.diffcount.renderer.gui.DiffCountFrame

### Antからの利用

1.6からはAntから呼び出すこともできます。この場合はfilesetで対象となるソースを選択します。
また、format属性で出力フォーマットを、output属性で出力ファイルを指定することができます。
format属性を省略した場合はデフォルトのフォーマットが使用されます。
output属性を省略した場合は標準出力へ出力されます。build.xmlは以下のようになります。

Excelフォーマッタを使用する場合、カスタムタスクのクラスパスにlibディレクトリに格納されている
JARファイル群も追加する必要があることに注意してください（Excelフォーマッタを使用しない場合、
stepcounterタスクはstepcounter.jarのみ、diffcounterタスクはstepcounter.jarとorg.apache.commons.jrcs.diff.jarのみで動作します）。

    <!-- 独自タスクの定義 -->
    <taskdef name="stepcounter"
      classname="jp.sf.amateras.stepcounter.ant.StepCounterTask"
      classpath="stepcounter-x.x.x-jar-with-dependencies.jar"/>

    <taskdef name="diffcounter"
      classname="jp.sf.amateras.stepcounter.ant.DiffCounterTask"
      classpath="stepcounter-x.x.x-jar-with-dependencies.jar"/>

    <target name="count">
      <!-- ステップ数をカウント -->
      <stepcounter format="csv" output="count.txt" encoding="UTF-8">
        <fileset dir="src">
          <include name="**/*.java"/>
        </fileset>
      </stepcounter>

      <!-- 差分をカウント -->
      <diffcounter format="csv" output="diff.txt" encoding="UTF-8"
         srcdir="current/src" olddir="old/src"/>
    </target>

### Eclipseプラグイン

jp.sf.amateras.stepcounter_x.x.x.jarをダウンロードし、Eclipseのpluginsディレクトリに配置してください。

パッケージ・エクスプローラーなどでファイルまたはディレクトリを選択してポップアップメニューから
[ステップカウンタ] > [ステップ数をカウント] でステップ数をカウントすることができます。

![Eclipseプラグイン（カウント結果）](https://github.com/takezoe/stepcounter/blob/master/eclipse_plugin.png?raw=true)

カウント結果は右クリックメニューからTSV形式でクリップボードにコピーしたり、Excelファイルとして保存することができます。

差分カウントはディレクトリを選択して [ステップカウンタ] > [差分をカウント] でカウントできます。
比較元のディレクトリを選択するダイアログが開くので、任意のディレクトリを選択してください。

![Eclipseプラグイン（差分カウント結果）](https://github.com/takezoe/stepcounter/blob/master/doff_count_view.png?raw=true)

差分カウントビューでは右クリックメニューからTSV形式でクリップボードにコピーしたり、Excelファイルとして保存することができます。

### 特殊なタグ

ソースコードに特殊なタグを記述しておくことでカウント結果をカテゴリ別に集計したり、カウント対象外にすることができます。

カテゴリ別に集計するにはソースコード中の任意の場所に以下のようなタグを記述しておきます。

    [[カテゴリ]]

Eclipseプラグインでは以下のようにカテゴリ別タブでカテゴリ別に集計された値を確認することができます。

また、同様にソースコードに以下のタグを記述しておくと、そのファイルはカウント結果から除外されます。

    [[IGNORE]]

これらのタグはステップカウント、差分カウントのどちらの場合でも有効です。

### Mavenでの利用

Mavenを使用している場合は以下の依存関係をpom.xmlに追加してください。

    <repositories>
      <repository>
        <id>amateras</id>
        <name>Project Amateras Maven2 Repository</name>
        <url>http://amateras.sourceforge.jp/mvn/</url>
      </repository>
    </repositories>

    <dependencies>
      <dependency>
        <groupId>jp.sf.amateras.stepcounter</groupId>
        <artifactId>stepcounter</artifactId>
        <version>3.0.0</version>
      </dependency>
    </dependencies>

更新履歴
----------------
### Version 3.0.0(未リリース)

* ソースコードのリポジトリをSourceForge.netのSubversionからGithubに移行しました。
  これにともなってパッケージ名やEclipseプラグインのプラグインIDなどを変更しました。
* ステップカウンタのコア部分とEclipseプラグイン部分を切り離し、コア部分をMavenリポジトリで提供するようにしました。
* コマンドライン版のステップカウンタにディレクトリのパスを含めて出力するための-showDirectoryオプションを追加しました。
  Antタスクではsrc要素でディレクトリを指定すると同じようにディレクトリのパスが出力されます。
* Eclipseプラグインでのファイルの文字コードの判別処理を改善しました。
* NetBeans版の開発を停止しました。

### Version 2.0.0(2010/11/14)

* ディレクトリの差分をカウントできるようにしました。
* Clojure、Scalaに対応しました。

新機能の詳細については[開発者のブログ](http://d.hatena.ne.jp/takezoe/20101114#p1)も参照してください。

### Version 1.16(2010/8/29)

* 読み込むソースの文字コードを指定できるようになりました（Eclipseプラグインの場合はワークスペースの設定から自動的に読み込みます）。
* カテゴリ別に集計する機能を追加しました。
* カウント対象外のファイルを指定できるようにしました。
* カウント結果をExcelファイルとして保存できるようにしました。

新機能の詳細については[開発者のブログ](http://d.hatena.ne.jp/takezoe/20100829#p1)も参照してください。

### Version 1.16(2010/1/31)

* .htmをHTMLファイルとしてカウントするようにしました
* .diconをXMLファイルとしてカウントするようにしました
* Python用のカウンタでdocstringをコメントとみなすようにしました
* VB.NET, C#に対応しました

### Version 1.14(2006/07/19)

* Python、Haskell、Luaに対応。
* NetBeans 5.0用モジュールも用意。

### Version 1.13(2004/09/11)

* EclipseプラグインをEclipse 3.0に対応させた。今回のバージョンからはEclipse 2.1系では動作しません。

### Version 1.12(2004/07/17)

* Swing版の場合に設定ファイルでテキストエリアのフォントを設定できるようにした(一度終了後、FontNameとFontSizeプロパティを書き変えてください)。
* Swing版でカウント後、再度Executeボタンを押下してもテキストエリアに何も出力されないバグを修正。
* TLDファイルをサポート。
* 複数行コメントが一行に記述されていており、その行にコメント以外の記述があった場合にコメント行としてカウントされてしまうバグを修正。

### Version 1.11(2004/06/25)

* Eclipseプラグインでカウント結果からファイルを開けるようにした。
* Eclipseプラグインで文字列をプロパティファイルから取得するようにした（英語と日本語のリソースを用意しています）。
* 拡張子がasaのファイルをASPファイルとしてカウントするようにした。

### Version 1.10(2004/05/01)

* フォーマッタのAPIを変更。XML形式、HTML形式のフォーマッタを削除した。
* VisualBasic、INIファイルに対応した。

### Version 1.9(2004/03/10)

* Eclipseプラグインで選択範囲をタブ区切りでクリップボードにコピーできるようにした。

### Version 1.8(2004/03/02)

* HTML形式、XML形式で出力できるようにした。
* Linuxでdefault形式で出力した場合にヘッダがズレてしまうバグを修正した。
* EclipseプラグインでJavaソース以外のファイル、フォルダをカウントできるようにした。
* Eclipseプラグインでカウント結果をソートできるようにした。

### Vesrion 1.7(2004/02/21)

* ファイルに出力できるようにした。
* CSV形式で出力できるようにした。
* Eclipseプラグインとして使用できるようにした。

### Version 1.6(2003/06/23)

* AntからStepCounterを呼び出す独自タスクを同梱。
* Perlのpmファイルに対応。いくつかの言語のコメント仕様を修正。

### Version 1.5(2003/03/18)

* CSS、Lisp、Velocityに対応。
* Perl、Rubyの複数行コメントに対応。

### Version 1.4(2002/12/27)

* 細部の修正のみ。

### Version 1.3(2002/07/31)

* XML、DTD、XMLSchema、Xi等に対応(Thanks to おいぬまさん)

### Version 1.2(2002/07/08)

* Perl、PHP、ColdFusion、Ruby、Tclに対応。
* 削除時に複数のファイルを選択できるよう修正。

### Version 1.1(2002/06/09)

* ASP、HTML、VBScript、JavaScriptに対応。
* 一度に複数のファイルを開けるように修正。
* 前回開いたフォルダを記憶するよう修正。

### Version 1.0(2002/05/02)

* Java、JSPファイルのみ対応。


ライセンス
----------------
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Copyright &copy; 2012 Project Amateras
