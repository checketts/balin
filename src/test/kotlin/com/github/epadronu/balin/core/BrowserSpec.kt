/******************************************************************************
 * Copyright 2016 Edinson E. Padrón Urdaneta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/

/* ***************************************************************************/
package com.github.epadronu.balin.core
/* ***************************************************************************/

/* ***************************************************************************/
import kotlin.test.assertEquals

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.jetbrains.spek.api.Spek
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import com.github.epadronu.balin.libs.delegatesTo
/* ***************************************************************************/

/* ***************************************************************************/
class BrowserSpec : Spek({
  given("the Kotlin's website index page URL") {
    val indexPageUrl = "http://kotlinlang.org/"

    on("visiting such URL") {
      var currentBrowserUrl: String? = null
      var currentPageTitle: String? = null

      Browser.drive(driver=HtmlUnitDriver(BrowserVersion.FIREFOX_45)) {
        currentBrowserUrl = to(indexPageUrl)
        currentPageTitle = title
      }

      it("should change the browser's URL to the given one") {
        assertEquals(indexPageUrl, currentBrowserUrl)
      }

      it("should get the title of the Kotlin's website index page") {
        assertEquals("Kotlin Programming Language", currentPageTitle)
      }
    }
  }

  given("the Kotlin's website index page URL and a couple of CSS selectors") {
    val indexPageUrl = "http://kotlinlang.org/"

    val bonusFeaturesSelector = "li.kotlin-feature > h3:nth-child(2)"
    val coolestFeaturesSelector = "li.kotlin-feature > h3:nth-child(2)"
    val navItemsSelector = "a.nav-item"
    val tryItBtnSelector = ".get-kotlin-button"

    on("visiting such URL and getting the elements for said selectors") {
      var bonusFeatures : List<String>? = null
      var coolestFeatures : List<String>? = null
      var navItems : List<String>? = null
      var tryItBtn : String? = null

      Browser.drive(driver=HtmlUnitDriver(BrowserVersion.FIREFOX_45)) {
        to(indexPageUrl)

        bonusFeatures = `$`(bonusFeaturesSelector, 4, 3).map { it.text }
        coolestFeatures = `$`(coolestFeaturesSelector, 0..2).map { it.text }
        navItems = `$`(navItemsSelector).map { it.text }
        tryItBtn = `$`(tryItBtnSelector, 0).text
      }

      it("should get the navigation items") {
        assertEquals(navItems, listOf("Learn", "Contribute", "Try Online"))
      }

      it("should get the try-it button") {
        assertEquals(tryItBtn, "Try Kotlin")
      }

      it("should get the coolest features") {
        assertEquals(coolestFeatures, listOf("Concise", "Safe", "Versatile"))
      }

      it("should get the bonus features") {
        assertEquals(bonusFeatures, listOf("Tooling", "Interoperable"))
      }
    }
  }

  given("the Kotlin's website index page with content elements and no URL") {
    class IndexPage : Page() {
      override val url = null

      override val at = delegatesTo<Browser, Boolean> {
        title == "Kotlin Programming Language"
      }

      val navItems by lazy {
        `$`("a.nav-item").map { it.text }
      }

      val tryItBtn by lazy {
        `$`(".get-kotlin-button", 0).text
      }

      val coolestFeatures by lazy {
        `$`("li.kotlin-feature > h3:nth-child(2)", 0..2).map { it.text }
      }

      val bonusFeatures by lazy {
        `$`("li.kotlin-feature > h3:nth-child(2)", 4, 3).map { it.text }
      }
    }

    on("visiting the index page URL and setting the browser's page with `at`") {
      var page: IndexPage? = null

      Browser.drive(driver=HtmlUnitDriver(BrowserVersion.FIREFOX_45)) {
        to("http://kotlinlang.org/")

        page = at(IndexPage::class.java).apply {
          // In order to execute the lazy evaluation and cache the results
          bonusFeatures; coolestFeatures; navItems; tryItBtn
        }
      }

      it("should change the browser's page to the given one") {
        assertEquals(true, page is IndexPage)
      }

      it("should get the navigation items") {
        assertEquals(
          page?.navItems, listOf("Learn", "Contribute", "Try Online")
        )
      }

      it("should get the try-it button") {
        assertEquals(page?.tryItBtn, "Try Kotlin")
      }

      it("should get the coolest features") {
        assertEquals(
          page?.coolestFeatures, listOf("Concise", "Safe", "Versatile")
        )
      }

      it("should get the bonus features") {
        assertEquals(page?.bonusFeatures, listOf("Tooling", "Interoperable"))
      }
    }
  }
})
/* ***************************************************************************/
