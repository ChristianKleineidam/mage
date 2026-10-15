# Original font defaults and startup sizing — 2026-10-15

Audited all six scaling PRs against upstream `cefedf905c4f13a03b4977da5de5c4b851884a7e`. The font calculations in GUISizeHelper are byte-for-byte identical to upstream. The eight preference keys, original fallback values, four preset tables, slider range 7–99 and settings-version gate are unchanged. Startup differs only by calling `applyPresetIfMissing` instead of replacing all saved sizes with a screen preset.

The original fallbacks remain 14 for dialog/menu and chat fonts, editor/player/card-zone size settings, and 17 for popup hints. Standard controls retain their form/look-and-feel fonts. Windows supplies native DPI scaling; it is not multiplied into these preferences. At native 200%, a 3840×2160 desktop has logical height 1080. Existing independent size choices survive startup and theme refresh, including an intentionally selected large font. The original explicit 3840×2160 preset still uses font size 34; the PRs neither force that preset onto saved settings nor silently reset users who chose it.

The reported oversized installed startup used stored/cached/calculated font size 34 from that old preset. A separate settings probe incorrectly appeared to confirm font size 14; it did not describe the effective settings of the normal launcher process. The actual Preferences dialog and read-only live JVM inspection both confirmed all eight old preset values. The requested local correction was made through XMage itself, then verified after a full exit and fresh launch through the regular installed launcher: stored, cached, calculated and Nimbus painting font size 14, native display transform 2, toolbar height 26 logical units. All eight corrected values persisted. This was a local preference correction, not a change to upstream defaults or a preference-reset migration.

This revision adds only two test-file changes. No production code, .form file, dependency, workflow, installed binary, user preference or image file changes are part of this revision. The installed combined build remains eeed1002 with the corrected real-client preferences; its executable source is identical to the new combined revision. Independent crash PR #16497 and downloader PR #16498 remain unchanged.

The five new GuiFontDefaultsTest cases pin the fallback fonts and card dimensions without saving them, all original preset values and key strings/order, preset selection for logical desktops at DPI 96/120/144/192, preservation of every saved size through all presets and theme refresh, and filling only missing sizes. The toolbar regression now starts from the generated form's font size 48 and verifies Nimbus paints font size 14 on the first sizing pass, then checks font/height and scrolling across the full 7–99 range. These are isolated helper and layout regressions; they do not launch a database-backed client as part of the test suite.

Both deliberate regressions were rejected: changing the default dialog font from 14 to 34, and making startup preset application overwrite saved values. The latter breaks two assertions; the former breaks the baseline assertion. Swing wraps EDT assertions in InvocationTargetException in the Maven report. Both injected source changes were restored before the final passing clean suites.

| PR / runtime | Cases | Passed | Existing skips | Failures / errors |
| --- | ---: | ---: | ---: | ---: |
| #16489 / Java 8 + bundled JavaFX | 54 | 43 | 11 | 0 / 0 |
| #16489 / Java 17 | 54 | 44 | 10 | 0 / 0 |
| #16489 / Java 21 | 54 | 44 | 10 | 0 / 0 |
| #16490 / Java 21 | 58 | 48 | 10 | 0 / 0 |
| #16491 / Java 21 | 57 | 47 | 10 | 0 / 0 |
| #16492 / Java 21 | 56 | 46 | 10 | 0 / 0 |
| #16493 / Java 21 | 60 | 50 | 10 | 0 / 0 |
| #16494 / Java 21 | 58 | 48 | 10 | 0 / 0 |
| Combined / Java 17 gui | 85 | 75 | 10 | 0 / 0 |
| Combined / Java 17 headless | 85 | 63 | 22 | 0 / 0 |

All clean branch suites keep the repository's existing skips; the five new tests run without skips. The headless combined run additionally skips display-dependent cases. Java release 8 remains unchanged.

- JVM display scale 1: 60 GUI/format checks, no failures.
- JVM display scale 1.25: 60 GUI/format checks, no failures.
- JVM display scale 1.5: 60 GUI/format checks, no failures.
- JVM display scale 2: 60 GUI/format checks, no failures.

These scale checks use isolated settings and the installed bundled Java 17 runtime with the new combined test/classes output and existing installed dependencies. They do not change Windows display settings or claim new physical mouse/keyboard testing. The separately described normal installed startup was observed at native 200%.

Foundation: `19a330e5c0377f6491df58f8aab0fca24e1384e9`. Combined: `ec3e0e9bc9246dfd1d98f1c20131156ec4de3ca3`.

- #16489: `19a330e5c0377f6491df58f8aab0fca24e1384e9`
- #16490: `5bab996ccce0ca5fa83f50b9cd4c0054f9898ce9`
- #16491: `9df185b5b4bd5780d28751303cdd06aa3366ad0c`
- #16492: `2c255e2419580b134db25301f9c7fd9c99560304`
- #16493: `375a82b2036a0a346b58011c5ecb01b0febfab38`
- #16494: `7c8b81ccdbb4f0f254792ee788dd26ed8dea143d`

Focused regression command after installing reactor dependencies:

```text
mvn -B -ntp -pl Mage.Client test "-Dtest=GuiFontDefaultsTest,GuiLayoutUtilTest" "-Djava.util.prefs.PreferencesFactory=mage.client.util.gui.GuiTestPreferencesFactory"
```

Upstream build/test workflows require maintainer approval. Local results are not upstream CI passes. No workflows were rerun, changed or approved.
