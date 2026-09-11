package com.orbits.data.notes

import org.commonmark.Extension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.Heading
import org.commonmark.node.Link
import org.commonmark.node.ListBlock
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Markdown parser for note content.
 * Supports CommonMark with GFM extensions (tables, strikethrough).
 */
@Singleton
internal class MarkdownParser @Inject constructor() {

    private val extensions: List<Extension> = listOf(
        TablesExtension.create(),
        StrikethroughExtension.create()
    )

    private val parser = Parser.builder()
        .extensions(extensions)
        .build()

    private val renderer = HtmlRenderer.builder()
        .extensions(extensions)
        .build()

    /**
     * Parse markdown to HTML.
     */
    fun parseToHtml(markdown: String): String {
        if (markdown.isBlank()) return ""

        return try {
            val document = parser.parse(markdown)
            renderer.render(document)
        } catch (e: Exception) {
            // Fallback to plain text if parsing fails
            markdown.replace("\n", "<br>")
        }
    }

    /**
     * Parse markdown to plain text (stripping markup).
     */
    fun parseToPlainText(markdown: String): String {
        if (markdown.isBlank()) return ""

        return try {
            val document = parser.parse(markdown)
            val textExtractor = TextExtractorVisitor()
            document.accept(textExtractor)
            textExtractor.getText()
        } catch (e: Exception) {
            markdown
        }
    }

    /**
     * Extract all tags from markdown content.
     * Looks for #hashtag patterns.
     */
    fun extractTags(markdown: String): List<String> {
        val tagRegex = Regex("#([A-Za-z0-9_\\-]+)")
        return tagRegex.findAll(markdown)
            .map { it.groupValues[1] }
            .distinct()
            .toList()
    }

    /**
     * Extract task references from markdown content.
     * Looks for @task(123) or @task-id patterns.
     */
    fun extractTaskReferences(markdown: String): List<String> {
        val taskRegex = Regex("@task\\(([^)]+)\\)|@task-([A-Za-z0-9\\-]+)")
        return taskRegex.findAll(markdown)
            .flatMap { match ->
                listOfNotNull(
                    match.groupValues.getOrNull(1)?.takeIf { it.isNotBlank() },
                    match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }
                )
            }
            .distinct()
            .toList()
    }

    /**
     * Extract checklist items from markdown.
     * Looks for - [ ] and - [x] patterns.
     */
    fun extractChecklist(markdown: String): List<ChecklistItem> {
        val checklistRegex = Regex("- \\[([ x])\\] (.*?)(?=\\n|$)")
        return checklistRegex.findAll(markdown)
            .map { match ->
                val checked = match.groupValues[1] == "x"
                val text = match.groupValues[2].trim()
                ChecklistItem(checked, text)
            }
            .toList()
    }

    /**
     * Visitor that extracts plain text from markdown AST.
     */
    private class TextExtractorVisitor : AbstractVisitor() {
        private val textBuilder = StringBuilder()

        fun getText(): String = textBuilder.toString().trim()

        override fun visit(text: Text) {
            textBuilder.append(text.literal)
        }

        override fun visit(softLineBreak: SoftLineBreak) {
            textBuilder.append(' ')
        }

        override fun visit(paragraph: Paragraph) {
            visitChildren(paragraph)
            textBuilder.append('\n')
        }

        override fun visit(heading: Heading) {
            visitChildren(heading)
            textBuilder.append('\n')
        }

        override fun visit(bulletList: org.commonmark.node.BulletList) {
            visitChildren(bulletList)
            textBuilder.append('\n')
        }

        override fun visit(orderedList: org.commonmark.node.OrderedList) {
            visitChildren(orderedList)
            textBuilder.append('\n')
        }

        override fun visit(listItem: ListItem) {
            textBuilder.append("• ")
            visitChildren(listItem)
            textBuilder.append('\n')
        }

        override fun visit(strongEmphasis: StrongEmphasis) {
            textBuilder.append("**")
            visitChildren(strongEmphasis)
            textBuilder.append("**")
        }

        override fun visit(code: Code) {
            textBuilder.append('`').append(code.literal).append('`')
        }

        override fun visit(fencedCodeBlock: FencedCodeBlock) {
            textBuilder.append("```").append(fencedCodeBlock.info).append('\n')
            textBuilder.append(fencedCodeBlock.literal)
            textBuilder.append("\n```\n")
        }

        override fun visit(link: Link) {
            textBuilder.append('[')
            visitChildren(link)
            textBuilder.append("](").append(link.destination).append(')')
        }
    }

    data class ChecklistItem(
        val checked: Boolean,
        val text: String
    )
}