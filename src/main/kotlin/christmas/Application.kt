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

    input.readDate()

    var order = input.readMenu()
    val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }

    println("12월 3일에 우테코 식당에서 받을 이벤트 혜택 미리 보기!")
    output.printMenu(order)
    val totalPrice = calculateTotalPrice(mappingOrder)
    output.printTotalPrice(mappingOrder, totalPrice)
    output.printGift(totalPrice)
}

fun checkGift(totalPrice : Int):Boolean {
    return totalPrice >= 120_000
}

fun checkMenuRules(mappingOrder : Map<String, Int>) {
    if (!checkOrderValidity(mappingOrder)) {
        throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
    }
}

class InputView {
    fun readDate(): Int {
        println("12월 중 식당 예상 방문 날짜는 언제인가요? (숫자만 입력해 주세요!)")
        val input = readlnOrNull() ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요")

        return try {
            val date = input.toIntOrNull() ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요")

            if (date !in 1..31) {
                throw IllegalArgumentException("[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요")
            }
            date
        } catch (e: IllegalArgumentException) {
            println(e.message)
            readDate()
        }
    }

    fun readMenu(): Map<String, Int> {
        return try {
            println("주문하실 메뉴를 메뉴와 개수를 알려 주세요. (e.g. 해산물파스타-2,레드와인-1,초코케이크-1)")
            val input = readln()
            val order = parseOrder(input)
            val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
            checkMenuRules(mappingOrder)

            order
        } catch(e: IllegalArgumentException) {
            println(e.message)
            readMenu()
        }
    }
}
fun calculateTotalPrice(order: Map<String, Int>): Int {
    var totalPrice = 0

    order.forEach { (item, quantity) ->
        totalPrice += when {
            item in Appetizer.entries.map { it.name } -> Appetizer.valueOf(item).price * quantity
            item in Main.entries.map { it.name } -> Main.valueOf(item).price * quantity
            item in Dessert.entries.map { it.name } -> Dessert.valueOf(item).price * quantity
            item in Beverage.entries.map { it.name } -> Beverage.valueOf(item).price * quantity
            else -> 0
        }
    }

    return totalPrice
}
val menuMapping = mapOf(
    "양송이수프" to "MUSHROOM_SOUP",
    "타파스" to "TAPAS",
    "시저샐러드" to "CAESAR_SALAD",
    "티본스테이크" to "T_BONE_STEAK",
    "바비큐립" to "BBQ_RIBS",
    "해산물파스타" to "SEAFOOD_PASTA",
    "크리스마스파스타" to "CHRISTMAS_PASTA",
    "초코케이크" to "CHOCOLATE_CAKE",
    "아이스크림" to "ICE_CREAM",
    "제로콜라" to "ZERO_COLA",
    "레드와인" to "RED_WINE",
    "샴페인" to "CHAMPAGNE"
)
fun translateToEnglishName(menuName: String): String {
    return menuMapping[menuName] ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
}
fun parseOrder(input: String): Map<String, Int> {
    return input.split(",").map { item ->
        val (menu, quantity) = item.split("-")
        menu.trim() to quantity.trim().toInt()
    }.toMap()
}

class OutputView {
    fun printMenu(orderedItems: Map<String, Int>) {
        println()
        println("<주문 메뉴>")
        orderedItems.forEach { (menu, quantity) ->
            println("${menu} ${quantity}개")
        }
    }

    fun printTotalPrice(mappingOrder: Map<String, Int>, totalPrice: Int) {
        println()
        println("<할인 전 총주문 금액>")
        val formatted = String.format("%,d", totalPrice)
        println("${formatted}원")
    }

    fun printGift(totalPrice: Int) {
        println()
        println("<증정 메뉴>")
        val isGift = checkGift(totalPrice)
        println(if (isGift) "샴페인 1개" else "없음")
    }
}

fun isMenuInList(order: Map<String, Int>): Boolean {
    val validMenuItems = Appetizer.entries.map { it.name } +
            Main.entries.map { it.name } +
            Dessert.entries.map { it.name } +
            Beverage.entries.map { it.name }
    return order.keys.all { it in validMenuItems }
}

fun isQuantityValid(order: Map<String, Int>): Boolean {
    return !order.values.any { it < 1 }
}

fun isTotalItemsWithinLimit(order: Map<String, Int>, limit: Int): Boolean {
    val totalItems = order.values.sum()
    return totalItems <= limit
}

fun hasNoDuplicateItems(order: Map<String, Int>): Boolean {
    return order.keys.distinct().size == order.size
}

fun hasValidCategory(order: Map<String, Int>): Boolean {
    val hasValidCategory = order.keys.any { it in Appetizer.entries.map { it.name } ||
            it in Main.entries.map { it.name } ||
            it in Dessert.values().map { it.name } }
    return hasValidCategory
}
fun checkOrderValidity(order: Map<String, Int>): Boolean {
    return isMenuInList(order) &&
            isQuantityValid(order) &&
            isTotalItemsWithinLimit(order, 20) &&
            hasNoDuplicateItems(order) &&
            hasValidCategory(order)
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
