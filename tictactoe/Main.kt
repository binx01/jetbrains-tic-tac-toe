package tictactoe

import java.lang.StringBuilder

open class Game(val name: String)

class Move(val priorCell: Cell?, val nextCell: Cell)

class Cell(private val row: Int, private val col: Int, var mark: Mark) {
    val coordinates: Pair<Int, Int>
        get() = Pair(row, col)
}

class Grid() {
    private var _cells = emptyArray<Array<Cell>>()
    val content: CharArray
        get() {
            var values = charArrayOf()
            _cells.forEach { cells -> cells.forEach { cell -> values += cell.mark.value } }
            return values
        }

    init {
        var x = 1
        var y = 1
        repeat(3) {
            _cells += arrayOf(Cell(x, y, Mark.EMPTY), Cell(x, ++y, Mark.EMPTY), Cell(x, ++y, Mark.EMPTY))
            ++x
            y = 1
        }
    }

    val rowsToWin = arrayOf(
        Triple(1, 2, 3),
        Triple(4, 5, 6),
        Triple(7, 8, 9),
        Triple(1, 4, 7),
        Triple(2, 5, 8),
        Triple(3, 6, 9),
        Triple(1, 5, 9),
        Triple(3, 5, 7),
    )

    fun show(): String {
        val sb = StringBuilder()
        repeat(9) { sb.append("-") }
        sb.append("\n")
        for (cells in _cells) {
            sb.append("| ${cells[0].mark.value} ${cells[1].mark.value} ${cells[2].mark.value} |\n")
        }
        repeat(9) { sb.append("-") }
        return sb.toString()
    }

    fun markCell(cellToMark: Cell) {
        if (_cells[cellToMark.coordinates.first - 1][cellToMark.coordinates.second - 1].mark != Mark.EMPTY) throw Error(
            "Occupied!"
        )
        _cells[cellToMark.coordinates.first - 1][cellToMark.coordinates.second - 1].mark = cellToMark.mark
    }

}

enum class Status(val result: String) {
    WINS_X("X wins"),
    WINS_O("O wins"),
    DRAW("Draw"),
    UNFINISHED("Unfinished")
}

enum class Mark(val value: Char) {
    X('X'),
    O('O'),
    EMPTY(' ')
}

class TicTacToe(private val grid: Grid = Grid()) : Game("Tic-Tac-Toe") {

    private var moves = mutableListOf<Move>()

    val status: Status
        get() {
            val content = grid.content
            val countOfX = content.count { it == 'X' }
            val countOfO = content.count { it == 'O' }
            val countOfEmpty = content.count { it == ' ' }
            var xHas3 = false
            var oHas3 = false

            for (row in grid.rowsToWin) {
                if (content[row.first - 1] == 'X' &&
                    content[row.first - 1] == content[row.second - 1] &&
                    content[row.second - 1] == content[row.third - 1]
                ) xHas3 = true
                if (content[row.first - 1] == 'O' &&
                    content[row.first - 1] == content[row.second - 1] &&
                    content[row.second - 1] == content[row.third - 1]
                ) oHas3 = true
            }

            return when {
                // xHas3 && oHas3 || abs(countOfX - countOfO) >= 2 -> print("Impossible")
                xHas3 && !oHas3 -> Status.WINS_X
                oHas3 && !xHas3 -> Status.WINS_O
                countOfX != 3 && countOfO != 3 && countOfEmpty == 0 -> Status.DRAW
                !xHas3 && !oHas3 && countOfEmpty > 0 -> Status.UNFINISHED
                else -> Status.UNFINISHED
            }
        }


    fun makeMove(row: Int, col: Int) {
        var nextMove: Move = if (moves.isNotEmpty()) {
            val lastMove = moves.last()
            if (lastMove.nextCell.mark == Mark.X) Move(lastMove.nextCell, Cell(row, col, Mark.O)) else
                Move(lastMove.nextCell, Cell(row, col, Mark.X))
        } else {
            Move(null, Cell(row, col, Mark.X))
        }
        moves.add(nextMove)
        grid.markCell(nextMove.nextCell)
    }

    fun showGrid(): String {
        return grid.show()
    }

}

fun main() {

    val game = TicTacToe()
    println(game.showGrid())

    do {
        print("Enter the coordinates: ")
        try {
            val (row, col) = readLine()!!.split(" ").map { it.toInt() }

            try {
                game.makeMove(row, col)
                println(game.showGrid())
            } catch (e: ArrayIndexOutOfBoundsException) {
                println("Coordinates should be from 1 to 3!")
            } catch (e: Error) {
                println(if (e.message == "Occupied!") "This cell is occupied! Choose another one!" else e.message)
            }

        } catch (e: NumberFormatException) {
            println("You should enter numbers!")
        }

    } while (game.status == Status.UNFINISHED)

    println(game.status.result)
}
