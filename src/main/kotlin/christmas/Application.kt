package christmas

fun main() {
    val input = InputView()
    val output = OutputView()

    val date = input.readDate()
    println("date : ${date}일")

    var order = input.readMenu()
    output.printMenu(order)


}

class InputView {
    fun readDate(): Int {
        println("12월 중 식당 예상 방문 날짜는 언제인가요? (숫자만 입력해 주세요!)")
        val input = readlnOrNull() ?: throw IllegalArgumentException("[ERROR] 입력을 받지 못했습니다")

        return try {
            input.toIntOrNull() ?: throw IllegalArgumentException("[ERROR] 숫자를 입력해주세요")
        } catch (e: IllegalArgumentException) {
            println(e.message)
            readDate()
        }
    }

    fun readMenu(): Map<String, Int> {
        println("주문하실 메뉴를 메뉴와 개수를 알려 주세요. (e.g. 해산물파스타-2,레드와인-1,초코케이크-1)")
        val input = readln().toString()
        val order = parseOrder(input)

        return order
    }
}

fun parseOrder(input: String): Map<String, Int> {
    return input.split(",").map { item ->
        val (menu, quantity) = item.split("-")
        menu.trim() to quantity.trim().toInt()
    }.toMap()
}

class OutputView {
    fun printMenu(orderedItems: Map<String, Int>) {
        println("<주문 메뉴>")
        orderedItems.forEach { (menu, quantity) ->
            println("${menu} ${quantity}개")
        }
    }
    // ...
}

enum class Appetizer(val price: Int) {
    MUSHROOM_SOUP(6_000),
    TAPAS(5_500),
    CAESAR_SALAD(8_000);
}

enum class Main(val price: Int) {
    T_BONE_STEAK(55_000),
    BBQ_RIBS(54_000),
    SEAFOOD_PASTA(35_000),
    CHRISTMAS_PASTA(25_000);
}

enum class Dessert(val price: Int) {
    CHOCOLATE_CAKE(15_000),
    ICE_CREAM(5_000);
}

enum class Beverage(val price: Int) {
    ZERO_COLA(3_000),
    RED_WINE(60_000),
    CHAMPAGNE(25_000);
}
