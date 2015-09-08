標準的なJavaプロジェクト
===

標準的なJavaプロジェクトのプロジェクト構成
===

```text
├── build.gradle
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   └── resources
    └── test
        ├── java
        └── resources
```

* プロジェクトの構成の規約
  * `build.gradle` -- ビルドの定義内容を記述したファイル
  * `settings.gradle` -- プロジェクトの設定を記述したファイル
  * `src/main/java` -- javaのプロダクション用ソースコードが配置されるディレクトリー
  * `src/main/resources` -- プロダクション用のリソースフィルが配置されるディレクトリー
  * `src/test/java` -- javaのテスト用ソースコードが配置されるディレクトリー
  * `src/test/resources` -- javaのテスト用リソースファイルが配置されるディレクトリー
* 設定よりも規約(Convention over Configuration)
  * Antではディレクトリーを設定する(Configuration)。
    * `<javac srcdir="src/main/java" destdir="build/classpath"/>`
  * Mavenではディレクトリーの構成は決まり事(Convention)として扱う。(構成を変更することもできる)
  * Gradleではディレクトリーの構成は決まり事(Convention)として扱う。(構成を変更することもできる)

標準的なビルドスクリプト
===

```groovy
apply plugin: 'java'
group = 'javajo.sample'
version = 1.0
defaultTasks('clean', 'build')
ext {
  javaVersion = 1.8
  encoding = 'UTF-8'
}
repositories {
  mavenCentral()
  maven {
    url 'https://repo.my-company.com/m2'
    credentials(PasswordCredentials) {
      username myRepositoryUser
      password myRepositoryPassword
    }
  }
}
dependencies {
  compile group: 'ch.qos.logback', name: 'logback-classic', version: '1.1.3', ext: 'jar'
  testCompile 'junit:junit:4.12'
  testCompile ('org.spockframework:spock-core:1.0-groovy-2.3') {
    exclude module: 'junit'
  }
}
tasks.withType(JavaCompile) {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
  options.encoding = encoding
}
```

### plugin部分

```groovy
apply plugin: 'java'
apply plugin: 'eclipse'
```

* Gradleではビルドに必要な機能を小さなプラグインの形式で提供しており、それらの組み合わせによってビルドを実現する。
  * 例
    * Javaのビルドが必要なら`java`プラグイン
    * IntelliJ IDEAで取り込むために`idea`プラグイン
    * Web archiveをビルドしたい場合は`war`プラグイン

### プロジェクト情報

作成されるアーカイブの情報を設定します。

* `group` - プロジェクトのグループ名。通常、パッケージ名と同様の名前を与える。
* `version` - プロジェクトのバージョン
* `description` - プロジェクトの説明
* `archiveBaseName` - (Javaプラグインにより追加される)jarファイルの名前。デフォルトはプロジェクトの名前が設定されている。

### デフォルトタスク

```groovy
defaultTasks 'clean', 'build'
```

* Gradleはタスクの集合体なので、Gradle起動時に実行するタスクを指定する必要があります。
* Gradle起動時に実行するタスクを指定していない場合には、デフォルトタスクに設定されたタスクが実行されるようになります。
* デフォルトのデフォルトタスクは`help`タスクです。

### Extra Properties

何らかの変数を利用したい場合は`ext`というオブジェクト(プロパティを管理しているExtra Propertiesというオブジェクト)に値を格納することで利用可能になります。

```groovy
ext.junitVersion = '4.12'
ext {
  groovyVersion = '2.4.4'
  javaVersion = '1.8'
}
```

ここで設定した値は、同一のビルドスクリプト内部で、名前空間の最初から指定することなく利用することができます。

```groovy
dependencies {
  compile "org.codehaus.groovy:groovy:${groovyVersion}"
  testCompile "junit:junit:${junitVersion}"
}
```

---

**Groovyの文字列？**

Groovyでは文字列(`String`)はシングルクオート`'`で囲むことで記述できます。

```groovy
String tokyo = '東京'
String kyoto = '京都'
```

Groovyでダブルクオート`"`で囲まれた文字列は`GString`というクラスのオブジェクトで、`String`を拡張した便利なオブジェクトです。

```groovy
// 文字列中で${}に記述された式を評価して文字列に埋め込むことができる
String tokyoToKyoto = "$tokyo-${kyoto}"
```

---

### 依存性管理

##### レポジトリー

依存ライブラリーを検索するためのレポジトリー情報を設定します。

```groovy
repositories {
  mavenCentral()
}
```

* `mavenCentral()`メソッド - 検索対象のレポジトリーにMavenセントラルを追加します。
* `jcenter()`メソッド - 検索対象のレポジトリーにjcenter(Bintrayが提供しているレポジトリー)を追加します。
* `mavenLocal()`メソッド - 検索対象のレポジトリーにローカルの`.m2`ディレクトリーを追加します。
* `maven{}`ブロック - 上記以外のレポジトリーを追加するための情報を設定します。
  * `url` - レポジトリーのURLを指定します。
  * `credentials` - 認証情報を指定します。
    * `PasswordCredential` - ベーシック認証情報を指定します。
      * `username` - ベーシック認証のユーザー名を指定します。
      * `password` - ベーシック認証のパスワードを指定します。
    * `AwsCredential` - AWSの認証情報を指定します(S3をレポジトリーに使っている場合)
      * `key` - AWSのアクセスキーを指定します
      * `secret` - AWSのアクセスシークレットを指定します

---

**ユーザー認証の情報はどこに保存しておくべき？**

Gradleはプロジェクトの評価前に設定情報を集めます。その際に下記の順番で値を集めていきます。

1. `$HOME/.gradle/gradle.properties`
1. `project/gradle.properties`

ユーザー認証情報などの他人には使われたくない情報は1.の`$HOME/.gradle/gradle.properties`に保存するべきです。

また、プロパティのキーと値の一覧を表示する`properties`タスクは`$HOME/.gradle/gradle.properties`の情報も表示するので、絶対に人前では実行しないでください。

---

