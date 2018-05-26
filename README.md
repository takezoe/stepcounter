StepCounter
======================
様々なプログラミング言語に対応したステップカウンタです。GUIとCUIの両方をサポートしており、AntやEclipseにも対応しています。

使い方
----------------
### コマンドライン

コマンドラインで使用するには[こちら](http://amateras.sourceforge.jp/mvn/jp/sf/amateras/stepcounter/stepcounter/)からstepcounter-x.x.x-jar-with-dependencies.jarをダウンロードし、コマンドラインから次のように入力してください。

```
> java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.Main [ファイル名] [ファイル名] ...
```

結果は標準出力に出力されます。ファイル名にはフォルダを指定することも可能で、
その場合そのフォルダに含まれる全てのファイルがカウント対象となりますので、
例えばJavaで複数のパッケージ内のファイルを一括カウントしたい場合などはトップのフォルダだけ指定してやればOKです。

-format=<出力形式>というオプションで出力形式を指定することができます。
また、-output=<ファイル名>というオプションを与えることで標準出力ではなくファイルへ出力を行ないます。
たとえばCSV形式でcount.txtファイルへの出力を行なうには以下のようにします。

```
> java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.Main -format=csv -output=count.txt -encoding=UTF-8 [ファイル名] [ファイル名] ...
```

指定可能なオプションは以下の通りです。

* -format: 出力フォーマットをcsv、excel、xml、jsonのいずれかで指定します。省略した場合はテキスト形式で出力します。
* -output: カウント結果を出力するファイルを指定します。省略した場合は標準出力に出力します。
* -encoding: ファイルの文字コードを指定します。省略した場合はプラットフォームのデフォルトエンコーディングを使用します。
* -showDirectory: trueを指定するとファイル名だけでなく起点となるディレクトリからの相対パスで表示します。

差分カウンタの場合は以下のように使用します。

```
> java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.diffcount.Main [新版のディレクトリ名] [旧版のディレクトリ名]
```

指定可能なオプションは以下の通りです。

* -format: 出力フォーマットをtext、html、excelのいずれかで指定します。省略した場合はテキスト形式で出力します。
* -output: カウント結果を出力するファイルを指定します。省略した場合は標準出力に出力します。
* -encoding: ファイルの文字コードを指定します。省略した場合はプラットフォームのデフォルトエンコーディングを使用します。

### Swingアプリケーション

Swingアプリケーション版を使用するにはコマンドラインから以下のように入力してください。

```
> java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.gui.MainWindow
```

差分カウンタの場合は以下のようにして起動します。

```
> java -cp stepcounter-x.x.x-jar-with-dependencies.jar jp.sf.amateras.stepcounter.diffcount.renderer.gui.DiffCountFrame
```

### Antからの利用

1.6からはAntから呼び出すこともできます。この場合はfilesetで対象となるソースを選択します。
また、format属性で出力フォーマットを、output属性で出力ファイルを指定することができます。
format属性を省略した場合はデフォルトのフォーマットが使用されます。
output属性を省略した場合は標準出力へ出力されます。build.xmlは以下のようになります。

```xml
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
```

stepcounterタスクでshowDirectory属性にtrueを指定することで、
コマンドライン版で-showDirectory=trueを指定した場合と同じように指定したディレクトリからの相対パスでファイル名を表示します。

### Eclipseプラグイン

[こちら](https://github.com/takezoe/stepcounter/releases)からjp.sf.amateras.stepcounter_x.x.x.jarをダウンロードし、Eclipseのpluginsディレクトリに配置してください。

パッケージ・エクスプローラーなどでファイルまたはディレクトリを選択してポップアップメニューから
[ステップカウンタ] > [ステップ数をカウント] でステップ数をカウントすることができます。

![Eclipseプラグイン（カウント結果）](https://github.com/takezoe/stepcounter/raw/master/eclipse_plugin.png)

カウント結果は右クリックメニューからTSV形式でクリップボードにコピーしたり、Excelファイルとして保存することができます。

差分カウントはディレクトリを選択して [ステップカウンタ] > [差分をカウント] でカウントできます。
比較元のディレクトリを選択するダイアログが開くので、任意のディレクトリを選択してください。

![Eclipseプラグイン（差分カウント結果）](https://github.com/takezoe/stepcounter/raw/master/diff_count_view.png)

差分カウントビューでは右クリックメニューからTSV形式でクリップボードにコピーしたり、Excelファイルとして保存することができます。

### 特殊なタグ

ソースコードに特殊なタグを記述しておくことでカウント結果をカテゴリ別に集計したり、カウント対象外にすることができます。

カテゴリ別に集計するにはソースコード中の任意の場所に以下のようなタグを記述しておきます。

```
[[カテゴリ]]
```

Eclipseプラグインでは以下のようにカテゴリ別タブでカテゴリ別に集計された値を確認することができます。

また、同様にソースコードに以下のタグを記述しておくと、そのファイルはカウント結果から除外されます。

```
[[IGNORE]]
```

これらのタグはステップカウント、差分カウントのどちらの場合でも有効です。

### Mavenでの利用

Mavenを使用している場合は以下の依存関係をpom.xmlに追加してください。

```xml
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
    <version>3.0.4</version>
  </dependency>
</dependencies>
```

更新履歴
----------------
### Version 3.0.4(2018/05/26)

 * コメントの処理を修正しました。
 * Eclipseプラグインに「選択範囲をカウント」メニューを追加しました。
 * Sass（*.sass）、SCSS（*.scss）に対応しました。
 * Groovy（*.groovy）に対応しました。
 * *.jspf, *.tag, *.jspx, *.tagx, *.ddl 拡張子に対応しました。
 * Makefileの判定が失敗する不具合と*.mkに対応しました。

### Version 3.0.3(2014/04/15)

 * シェルスクリプト（*.sh）に対応しました。
 * 差分カウンタのバグを修正しました。

### Version 3.0.2(2013/06/15)

 * ActionScript3とMXMLに対応しました。
 * C++/Objective-C/Objective-C++の拡張子(.cxx, .cc, .hpp, .hxx, .m, .mm)に対応しました。
 * 差分カウント用のライブラリをcommons-jrcsからjava-diff-utils ( https://code.google.com/p/java-diff-utils/ )に変更しました。

### Version 3.0.1(2012/4/21)

* カウント結果のフォーマッタにxmlとjsonを追加しました。
* Antタスクでfilesetだけでなく、filelistも使用可能になりました。
* Antタスクでの相対パス表示の指定方法を変更しました。3.0.0で追加されたsrc要素は廃止され、
  代わりにstepcounterタスクのshowDirectory属性にtrueを指定することで
  filesetやfilelistのdir属性で指定したディレクトリからの相対パスでファイル名を表示します。
* AntタスクにdefaultExcludes属性を追加しました。デフォルトで有効になります。
* AntタスクにdirectoryAsCategory属性を追加しました。
  標準では、カテゴリはファイル内のタグを見て判断されますが、この属性をtrueにするとカテゴリとして起点ディレクトリが利用されます。
  （srcとtestなど）起点ディレクトリによって分類したい場合に使用します。
* Antタスクにfailonerror属性を追加しました。
  他のタスクと同じように、ファイルI/Oに失敗した場合、処理を継続するか否かを設定できるようになりました。
* Fortran、Makefileに対応しました。

### Version 3.0.0(2012/2/18)

* ソースコードのリポジトリをSourceForge.netのSubversionからGithubに移行しました。
  これにともなってパッケージ名やEclipseプラグインのプラグインIDなどを変更しました。
* ステップカウンタのコア部分とEclipseプラグイン部分を切り離し、コア部分をMavenリポジトリで提供するようにしました。
* コマンドライン版のステップカウンタにディレクトリのパスを含めて出力するための-showDirectoryオプションを追加しました。
  Antタスクではsrc要素でディレクトリを指定すると同じようにディレクトリのパスが出力されます。
* Eclipseプラグインでのファイルの文字コードの判別処理を改善しました。
* Eclipseプラグインでカウント結果の重複を省くようにしました。
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
