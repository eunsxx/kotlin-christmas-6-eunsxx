package christmas

import camp.nextstep.edu.missionutils.Console
import java.time.DayOfWeek
import java.time.LocalDate

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

const val START_DISCOUNT_PRICE = 1_000
const val DISCOUNT_PRICE = 100
const val WEEK_DISCOUNT_PRICE = 2_023

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
fun main() {
    val (date, order) = readInput()
    val output = OutputView()
    val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }

    output.printMenuMessage(date, order)

    val totalPrice = calculateTotalPrice(mappingOrder)
    output.printEventList(totalPrice, date, mappingOrder)

}

fun readInput(): Pair<Int, Map<String, Int>> {
    val input = InputView()
    val date = input.readDate()
    val order = input.readMenu()
    return Pair(date, order)
}
class InputView {
    fun readDate(): Int {
        return try {
            println("12월 중 식당 예상 방문 날짜는 언제인가요? (숫자만 입력해 주세요!)")
            val input = Console.readLine()
            val date = input.toIntOrNull() ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요.")
            require(date in 1..31) { "[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요." }
            date
        } catch (e: IllegalArgumentException) {
            println(e.message)
            readDate()
        }
    }

    fun readMenu(): Map<String, Int> {
        return try {
            println("주문하실 메뉴를 메뉴와 개수를 알려 주세요. (e.g. 해산물파스타-2,레드와인-1,초코케이크-1)")
            val input = Console.readLine() ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
            val order = parseOrder(input)
            val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
            checkMenuRules(mappingOrder)

            order
        } catch (e: IllegalArgumentException) {
            println(e.message)
            readMenu()
        }
    }
}
class OutputView {
    fun printMenu(orderedItems: Map<String, Int>) {
        println()
        println("<주문 메뉴>")
        orderedItems.forEach { (menu, quantity) ->
            println("$menu ${quantity}개")
        }
    }

    fun printTotalPrice(totalPrice: Int) {
        println()
        println("<할인 전 총주문 금액>")
        val formatted = formatMoney(totalPrice)
        println("${formatted}원")
    }

    fun printGift(totalPrice: Int) {
        println()
        println("<증정 메뉴>")
        val isGift = checkGift(totalPrice)
        println(if (isGift) "샴페인 1개" else "없음")
    }

    fun printBenefit(date: Int, totalPrice: Int, order: Map<String, Int>){
        println()
        println("<혜택 내역>")
        val isEvent = checkEvent(totalPrice)
        if (!isEvent) {
            println("없음")
            return
        }
        printChristmasDDayEvent(date)
        printWeekdayEvent(order, date)
        printSpecialDiscount(date)
        printGiftEvent(totalPrice)
    }

    private fun printChristmasDDayEvent(date: Int){
        val xmasDiscount = calculateChristmasDDayEvent(date)
        val formatted = formatMoney(xmasDiscount)
        println("크리스마스 디데이 할인: -${formatted}")
    }

    private fun printWeekdayEvent(order: Map<String, Int>, date: Int) {
        val year = 2023
        val month = 12
        val isWeekend = checkWeekend(year, month, date)

        val totalDiscount = calculateWeekdayEvent(order, date)
        val formatted = formatMoney(totalDiscount)
        println("${if (isWeekend) "주말" else "평일"} 할인: -${formatted}원")
    }

    private fun printSpecialDiscount(date: Int) {
        val discounted = calculateSpecialDiscount(date)
        if (discounted == 0) return
        val formatted = formatMoney(discounted)
        println("특별 할인: -${formatted}원")
    }

    private fun printGiftEvent(totalPrice: Int) {
        val discounted = calculateGiftEvent(totalPrice)
        if (discounted == 0) return
        val formatted = formatMoney(discounted)
        println("증정 이벤트: -${formatted}원")
    }

    fun printTotalDiscount(totalDiscount: Int) {
        println()
        println("<총혜택 금액>")
        val formatted = formatMoney(totalDiscount)
        if (totalDiscount == 0) {
            println("0원")
            return
        }
        println("-${formatted}원")
    }

    fun estimatedPaymentAmount(totalPrice: Int, totalDiscount: Int) {
        var paymentAmount = totalPrice - totalDiscount
        println()
        println("<할인 후 예상 결제 금액>")
        if (totalPrice >= 120_000) paymentAmount += 25_000
        val formatted = formatMoney(paymentAmount)
        println("${formatted}원")
    }

    fun printEventBedge(totalDiscount: Int) {
        val bedge = selectEventBedge(totalDiscount)
        println()
        println("<12월 이벤트 배지>")
        println(bedge)
    }

    fun printMenuMessage(date: Int, order: Map<String, Int>) {
        println("12월 ${date}일에 우테코 식당에서 받을 이벤트 혜택 미리 보기!")
        printMenu(order)
    }

    fun printEventList(totalPrice: Int, date: Int, order: Map<String, Int>) {
        printTotalPrice(totalPrice)
        printGift(totalPrice)
        printBenefit(date, totalPrice, order)

        val totalDiscount = discountedTotal(date, totalPrice, order)
        printTotalDiscount(totalDiscount)
        estimatedPaymentAmount(totalPrice, totalDiscount)
        printEventBedge(totalDiscount)
    }
}

fun translateToEnglishName(menuName: String): String {
    return menuMapping[menuName] ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
}
fun parseOrder(input: String): Map<String, Int> {
    return input.split(",").map { item ->
        val parts = item.split("-")
        if (parts.size != 2) throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
        val menu = parts[0].trim()
        val quantity = parts[1].trim().toIntOrNull() ?: throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
        menu to quantity
    }.toMap()
}
fun discountedTotal(date: Int, totalPrice: Int, order: Map<String, Int>) :Int {
    if (!checkEvent(totalPrice)) return 0
    val discountFunctions = listOf(
        { calculateChristmasDDayEvent(date) },
        { calculateWeekdayEvent(order,date) },
        { calculateSpecialDiscount(date) },
        { calculateGiftEvent(totalPrice) }
    )

    return discountFunctions.sumOf { it() }

}

fun christmasDDayDiscount(date: Int): Int {
    if (date !in 1..25) return 0
    return START_DISCOUNT_PRICE + ((date - 1) * DISCOUNT_PRICE)
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
fun calculateChristmasDDayEvent(date: Int) :Int{
    val xmasDiscount = christmasDDayDiscount(date)
    if (xmasDiscount == 0) return 0
    return xmasDiscount
}
fun calculateWeekdayEvent(order: Map<String, Int>, date: Int) : Int {
    val year = 2023
    val month = 12
    val isWeekend = checkWeekend(year, month, date)

    val totalDiscount = if (isWeekend) {
        calculateWeekendDiscount(order)
    } else {
        calculateWeekdayDiscount(order)
    }

    return totalDiscount
}
fun calculateSpecialDiscount(date: Int) :Int {
    if(!isStarSpecialDay(date)) return 0
    return 1000
}
fun calculateGiftEvent(totalPrice: Int) :Int {
    val isGift = checkGift(totalPrice)
    if (!isGift) return 0
    return 25_000
}

fun checkWeekend(year: Int, month: Int, day: Int): Boolean {
    val date = LocalDate.of(year, month, day)
    val dayOfWeek = date.dayOfWeek

    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}
fun checkGift(totalPrice : Int):Boolean {
    return totalPrice >= 120_000
}
fun checkEvent(totalPrice: Int) : Boolean {
    return totalPrice >= 10_000
}
fun checkMenuRules(mappingOrder : Map<String, Int>) {
    if (!checkOrderValidity(mappingOrder)) {
        throw IllegalArgumentException("[ERROR] 유효하지 않은 주문입니다. 다시 입력해 주세요.")
    }
}
fun selectEventBedge(totalDiscount: Int): String {
    if (totalDiscount >= 20_000) return "산타"
    if (totalDiscount >= 10_000) return "트리"
    if (totalDiscount >= 5_000) return "별"
    return "없음"
}
fun isStarSpecialDay(date:Int) : Boolean{
    val specialDays = setOf(3, 10, 17, 24, 25, 31)
    return (date in specialDays)
}
fun calculateWeekendDiscount(order: Map<String, Int>): Int {
    return order.filter { (item, _) -> item in Main.entries.map { it.name } }
        .values.sum() * WEEK_DISCOUNT_PRICE
}
fun calculateWeekdayDiscount(order: Map<String, Int>): Int {
    return order.count { (item, _) -> item in Dessert.entries.map { it.name } } * WEEK_DISCOUNT_PRICE
}
fun formatMoney(discount: Int): String {
    return String.format("%,d", discount)
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
            it in Dessert.entries.map { it.name } }
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
