package io.hlab.vectormap.compose.settings

/**
 * 지도 객체의 Poi 표현 언어를 결정합니다.
 *
 * - [Korean] : Poi 를 한글로 표시합니다.
 * - [English] : Poi 를 영어로 표시합니다.
 */
enum class PoiLanguage(internal val code: String) {
    Korean("ko"), English("en")
}