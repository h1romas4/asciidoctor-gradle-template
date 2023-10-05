import java.io.File
import groovy.io.FileType
import org.asciidoctor.Asciidoctor
import org.asciidoctor.SafeMode
import org.asciidoctor.Options
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Input

/**
 * Asciidoc 文章とファイルシステムの画像の整合性を確認する Gradle タスク
 *
 * - 未使用画像の抽出
 * - リンク切れ画像の抽出
 */
abstract class SyncImageTask extends DefaultTask {
    /**
     * 処理起点ディレクトリ
     */
    @InputDirectory
    File baseDir

    /**
     * 起点 Asciidoc 文書（include も処理する）
     */
    @Input
    def index

    /**
     * 画像として認識するファイルシステム上の拡張子
     */
    @Input
    def imageExt = ['png', 'jpg']

    /**
     * 未使用画像をファイルシステムから削除するかどうかを決定するフラグ（TODO: 未実装）
     */
    @Input
    boolean remove = false

    /**
     * Asciidoc 文書から image ブロックの画像パスをリスト取得
     *
     * @param File baseDir Asciidoc 文書が格納されているディレクトリ
     * @param String index 起点となる Asciidoc 文書（この文書から include を辿る）
     * @return [] baseDir からの相対画像パスリスト
     */
    static def searchImageInAdoc(baseDir, index) {
        def asciidoctor = Asciidoctor.Factory.create();
        // include パスを辿るために SafeMode.SAFE と baseDir を設定
        def options = Options.builder().safe(SafeMode.SAFE).baseDir(baseDir).build()
        def document = asciidoctor.load(new File("${baseDir}/${index}").getText(), options);

        document.findBy(["context": ":image"]).collect {
            "${it.attributes.imagesdir}/${it.attributes.target}"
        }
    }

    /**
     * ディレクトリから指定された拡張子のファイルをリストで返却
     *
     * @param File baseDir 検索ベースディレクトリ
     * @param extension 検索拡張子文字列リスト
     * @return [] baseDir からの相対パスリスト
     */
    static def searchExtInDir(File baseDir, extension) {
        def list = []
        def basePath = "${baseDir}" + File.separator
        def ext = extension.collect {
            ".${it}"
        }
        baseDir.eachFileRecurse(FileType.FILES) { file ->
            ext.each {
                if(file.name.endsWith(it)) {
                    list << (file.path - basePath)
                }
            }
        }
        list
    }

    /**
     * リスト形式の文字列を表示用に縦に出力
     *
     * @param lists 文字列リスト
     * @param prefix 前置文字列
     */
    static def printls(lists, prefix = "") {
        lists.each {
            println "${prefix}${it}"
        }
    }

    /**
     * パス区切りを UNIX に正規化
     *
     * @param lists 文字列リスト
     */
    static def normalizePath(lists) {
        lists.collect {
            it.replace(File.separator, "/")
        }
    }

    /**
     * Gradle タスクエントリーポイント
     */
    @TaskAction
    def checkSyncImage() {
        def imagesInAdoc = normalizePath(searchImageInAdoc(baseDir, index).sort())
        def imagesInDir = normalizePath(searchExtInDir(baseDir, imageExt).sort())

        def unused = imagesInDir - imagesInAdoc
        def notFound = imagesInAdoc - imagesInDir

        if(unused.size() != 0) {
            println "::Unused images:"
            printls unused
        }
        if(notFound.size() != 0) {
            println "::Image not found:"
            printls notFound
        }
    }
}
