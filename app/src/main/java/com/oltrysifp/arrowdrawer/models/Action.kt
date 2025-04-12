package com.oltrysifp.arrowdrawer.models

open class Action

data class DeleteAction(
    val line: Line
): Action()

data class AddAction(
    val line: Line
): Action()

data class ChangeAction(
    val startLine: Line,
    val endLine: Line
): Action()

fun undoAction(
    action: Action,
    lineList: MutableList<Line>,
    focusedLine: Line?,
    focusedSetter: (Line?) -> Unit
) {
    if (action is DeleteAction) {
        lineList.add(action.line)
        if (focusedLine == null) { focusedSetter(action.line) }
    } else if (action is AddAction) {
        lineList.removeIf { it.hash == action.line.hash }
        focusedSetter(null)
    } else if (action is ChangeAction) {
        val index = lineList.indexOfFirst { it.hash == action.endLine.hash }
        lineList[index] = action.startLine
        focusedSetter(action.startLine)
    }
}