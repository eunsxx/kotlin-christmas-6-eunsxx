package christmas

const val MUSHROOM_SOUP_PRICE = 6_000
const val TAPAS_PRICE = 5_500
const val CAESAR_SALAD_PRICE = 8_000

const val T_BONE_STEAK_PRICE= 55_000
const val BBQ_RIBS_PRICE = 54_000
const val SEAFOOD_PASTA_PRICE = 35_000
const val CHRISTMAS_PASTA_PRICE = 25_000

const val CHOCOLATE_CAKE_PRICE = 15_000
const val ICE_CREAM_PRICE = 5_000

const val ZERO_COLA_PRICE = 3_000
const val RED_WINE_PRICE = 60_000
const val CHAMPAGNE_PRICE = 25_000

fun main() {
    val input = InputView()
    val output = OutputView()

    val date = input.readDate()
    println("date : ${date}일")

    var order = input.readMenu()
    output.printMenu(order)

    val isValid = checkOrderValidity(order)
    println(if (isValid) "유효한 주문입니다." else "주문에 유효하지 않은 항목이 포함되어 있습니다.")

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

fun isValidMenuItem(order: Map<String, Int>): Boolean {
    val appetizerMenuItems = setOf("양송이수프", "타파스", "시저샐러드")
    val mainMenuItems = setOf("티본스테이크", "바비큐립", "해산물파스타", "크리스마스파스타")
    val dessertMenuItems = setOf("초코케이크", "아이스크림")
    val beverageMenuItems = setOf("제로콜라", "레드와인", "샴페인")

    // 모든 메뉴 항목을 하나의 집합으로 합칩니다.
    val allMenuItems = appetizerMenuItems + mainMenuItems + dessertMenuItems + beverageMenuItems

    // 주문된 아이템이 전체 메뉴 집합에 포함되어 있는지 검사합니다.
    if (!order.keys.all { it in allMenuItems }) {
        return false // 유효하지 않은 메뉴 항목이 포함되어 있음
    }

    // 애피타이저, 메인, 디저트 중 하나라도 주문되었는지 확인합니다.
    val hasValidCategory = order.keys.any { it in appetizerMenuItems || it in mainMenuItems || it in dessertMenuItems }

    // 음료만 주문한 경우는 무효로 처리합니다.
    return hasValidCategory
}

fun checkOrderValidity(order: Map<String, Int>): Boolean {
    if (!isValidMenuItem(order)) return false
    return true
}

enum class Appetizer(val price: Int) {
    MUSHROOM_SOUP(MUSHROOM_SOUP_PRICE),
    TAPAS(TAPAS_PRICE),
    CAESAR_SALAD(CAESAR_SALAD_PRICE);
}

enum class Main(val price: Int) {
    T_BONE_STEAK(T_BONE_STEAK_PRICE),
    BBQ_RIBS(BBQ_RIBS_PRICE),
    SEAFOOD_PASTA(SEAFOOD_PASTA_PRICE),
    CHRISTMAS_PASTA(CHRISTMAS_PASTA_PRICE);
}

enum class Dessert(val price: Int) {
    CHOCOLATE_CAKE(CHOCOLATE_CAKE_PRICE),
    ICE_CREAM(ICE_CREAM_PRICE);
}

enum class Beverage(val price: Int) {
    ZERO_COLA(ZERO_COLA_PRICE),
    RED_WINE(RED_WINE_PRICE),
    CHAMPAGNE(CHAMPAGNE_PRICE);
}
