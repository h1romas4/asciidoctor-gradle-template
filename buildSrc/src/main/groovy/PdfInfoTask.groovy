import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFile

/**
 * PDF 文書のメタデータを確認するユーティリティタスク
 *
 */
abstract class PdfInfoTask extends DefaultTask {
    /**
     * PDF 文書
     */
    @InputFile
    File pdfFile

    /**
     * Gradle タスクエントリーポイント
     */
    @TaskAction
    def checkPdfInfo() {
    }
}
