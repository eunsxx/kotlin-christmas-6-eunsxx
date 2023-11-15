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




    override fun runMain() {
        main()
    }

    companion object {
        private val LINE_SEPARATOR = System.lineSeparator()
    }
}