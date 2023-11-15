package christmas

import camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest
import camp.nextstep.edu.missionutils.test.NsTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import java.io.ByteArrayInputStream

class InputViewTest: NsTest() {
    @Test
    fun `유효한 날짜 테스트`() {
        val inputView = InputView()
        val validDate = "15"
        System.setIn(ByteArrayInputStream(validDate.toByteArray()))
        assertEquals(15, inputView.readDate())
    }

    @Test
    fun `유효하지 않은 날짜 (32일 이상)를 입력했을 때 예외가 발생하는지 확인`() {
        assertSimpleTest {
            runException("32")
            assertThat(output()).contains("[ERROR] 유효하지 않은 날짜입니다. 다시 입력해 주세요.")
        }
    }

    @Test
    fun `올바른 형식의 문자열 파싱`() {
        val input = "티본스테이크-1,해산물파스타-2"
        val result = parseOrder(input)
        assertEquals(2, result.size)
        assertEquals(1, result["티본스테이크"])
        assertEquals(2, result["해산물파스타"])
    }

    @Test
    fun `잘못된 형식의 문자열 파싱`() {
        val input = "티본스테이크,해산물파스타-2"
        assertThrows<IllegalArgumentException> {
            parseOrder(input)
        }
    }

    @Test
    fun `유효한 주문 검사`() {
        val order = mapOf("티본스테이크" to 1, "해산물파스타" to 2)
        val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
        assertTrue(checkOrderValidity(mappingOrder))
    }

    @Test
    fun `수량이 0인 주문 검사`() {
        val order = mapOf("티본스테이크" to 0, "해산물파스타" to 2)
        val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
        assertFalse(checkOrderValidity(mappingOrder))
    }

    @Test
    fun `중복된 메뉴가 있는 주문 검사`() {
        val order = mapOf("티본스테이크" to 1, "티본스테이크" to 1, "해산물파스타" to 2)
        assertFalse(checkOrderValidity(order))
    }

    @Test
    fun `음료 메뉴만 주문한 경우 검사`() {
        val beverageOnlyOrder = mapOf("제로콜라" to 2, "레드와인" to 1)
        val mappingOrder = beverageOnlyOrder.mapKeys { (key, _) -> translateToEnglishName(key) }
        assertFalse(checkOrderValidity(mappingOrder))
    }

    @Test
    fun `총 주문 메뉴가 20개를 초과하는 경우 검사`() {
        val largeOrder = mapOf("티본스테이크" to 15, "해산물파스타" to 11)
        val mappingOrder = largeOrder.mapKeys { (key, _) -> translateToEnglishName(key) }
        assertFalse(checkOrderValidity(mappingOrder))
    }

    @Test
    fun `다양한 메뉴와 수량에 대한 총 주문 금액 계산`() {
        val order = mapOf(
            "티본스테이크" to 1,
            "해산물파스타" to 2,
            "초코케이크" to 3,
            "제로콜라" to 4
        )
        val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
        val expectedTotalPrice = T_BONE_STEAK_PRICE + (SEAFOOD_PASTA_PRICE * 2) + (CHOCOLATE_CAKE_PRICE * 3) + (ZERO_COLA_PRICE * 4)
        assertEquals(expectedTotalPrice, calculateTotalPrice(mappingOrder))
    }

    @Test
    fun `크리스마스 디데이 할인 계산`() {
        val date = 5 // 12월 5일
        val expectedDiscount = START_DISCOUNT_PRICE + ((date - 1) * DISCOUNT_PRICE)
        assertEquals(expectedDiscount, calculateChristmasDDayEvent(date))
    }

    @Test
    fun `주말 할인 계산`() {
        val order = mapOf("티본스테이크" to 2) // 주말에 티본스테이크 2개 주문
        val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }
        val date = 3 // 주말 (일요일)
        val expectedDiscount = 2 * WEEK_DISCOUNT_PRICE
        assertEquals(expectedDiscount, calculateWeekdayEvent(mappingOrder, date))
    }

    @Test
    fun `특별 할인 계산`() {
        val specialDay = 17 // 특별 할인이 적용되는 날
        val expectedDiscount = 1000 // 특별 할인 금액
        assertEquals(expectedDiscount, calculateSpecialDiscount(specialDay))
    }

    @Test
    fun `증정 이벤트 계산`() {
        val totalPrice = 130_000 // 12만원 이상 주문 시 증정 이벤트 적용
        val expectedDiscount = 25_000 // 증정 이벤트 할인 금액
        assertEquals(expectedDiscount, calculateGiftEvent(totalPrice))
    }

    @Test
    fun `총 할인 금액 계산`() {
        val date = 10 // 12월 10일
        val totalPrice = 150_000
        val order = mapOf("티본스테이크" to 1, "초코케이크" to 2)
        val mappingOrder = order.mapKeys { (key, _) -> translateToEnglishName(key) }

        val totalDiscount = discountedTotal(date, totalPrice, mappingOrder)

        val expectedDiscount = calculateChristmasDDayEvent(date) +
                calculateWeekdayEvent(mappingOrder, date) +
                calculateSpecialDiscount(date) +
                calculateGiftEvent(totalPrice)

        assertEquals(expectedDiscount, totalDiscount)
    }

    override fun runMain() {
        main()
    }

    companion object {
        private val LINE_SEPARATOR = System.lineSeparator()
    }
}