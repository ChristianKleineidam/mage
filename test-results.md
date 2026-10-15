# XMage GUI scaling validation

Recorded locally on 2026-10-15. No GitHub Actions result is claimed by these local measurements.

Upstream baseline: `492369c7844b8d76479f0393ff9eb6b0b0cf30d7`.
Tested combined build: `5eeec1b3c438b365263076c0cd1beb473ee4803d`.
Shared foundation: `415031adc90833dfffdba16901e6a5f2e2cdde22`.

## Isolated clean Java 21 client suites

| Contribution | Commit | Total | Passed | Skipped |
| --- | --- | ---: | ---: | ---: |
| Font/layout foundation | `415031adc90833dfffdba16901e6a5f2e2cdde22` | 42 | 32 | 10 |
| Draft divider | `c3d08552a501d176ea9078a236529a6826166f46` | 46 | 36 | 10 |
| Damage distribution | `e765bf649dad321fb8263d5953104e44c1ade3a5` | 45 | 35 | 10 |
| Popup placement | `deed12e0599832cf8a8d08d2fbae0e4b301c3afd` | 44 | 34 | 10 |
| Device-resolution rendering | `d68feec13f944006e29d182759c01864ce422815` | 48 | 38 | 10 |
| Deck-filter interaction | `96f172a61431103c4510980549d255b94b5c52c0` | 46 | 36 | 10 |

Every row has zero failures/errors. The five follow-up branches include the exact shared foundation above.

## Combined-build runtime checks

| Runtime/session | Total | Passed | Skipped |
| --- | ---: | ---: | ---: |
| Windows Java 17 / Java 21 (each) | 61 | 51 | 10 |
| Windows actual 125% / 150%, Java 17 (each) | 61 | 51 | 10 |
| Java 8 with bundled JavaFX | 61 | 50 | 11 |
| Ubuntu WSL/WSLg Java 17 | 61 | 50 | 11 |
| Headless Windows / Ubuntu Java 17 (each) | 61 | 40 | 21 |
| Headless Java 8 with bundled JavaFX | 61 | 39 | 22 |

All have zero failures/errors. The full 40-module local reactor build also passes with tests skipped for that build; client test coverage is the separate suite runs above. Java release 8, dependencies and server protocol are retained.

The ten pre-existing `@Ignore` cases are eight `DownloaderTest` methods plus the `MultiConnectTest` and `StartMultiGamesTest` class cases. Java 8 bundled FX additionally skips the Maven-JavaFX compatibility case because its bundled classes shadow those jars. WSL additionally skips the hardware peer-transition case because an ordinary hidden `JFrame` does not change graphics configuration when moved. Headless runs skip eleven native-window GUI cases.

All 15 edited NetBeans forms loaded/saved with forced Java regeneration through NetBeans 8.2 on private copies. The generated Java compiled with release 8 and passed all 36 GUI regressions. This verifies regeneration, not interactive palette use. Earlier theme checks cover six themes at four font sizes (24 combinations).

## Reproduction

With a supported development JDK and `JAVA_HOME` configured, from the checked-out contribution branch:

```text
mvn -B -ntp -pl Mage.Client -am install -DskipTests
mvn -B -ntp -pl Mage.Client clean test "-Djava.util.prefs.PreferencesFactory=mage.client.util.gui.GuiTestPreferencesFactory"
mvn -B -ntp -pl Mage.Client test "-Djava.awt.headless=true" "-Djava.util.prefs.PreferencesFactory=mage.client.util.gui.GuiTestPreferencesFactory"
```

The first command installs dependencies; it is not a test run. Apply focused `-Dtest` selections to `Mage.Client` after dependency installation. The tracked preference factory avoids personal preference changes. Headless execution does not establish native input/display behavior.

## Native acceptance and limits

Windows checks used actual 100%, 125%, 150% and 200% display scaling, plus mixed-display movement. At 125%/150% and font 34, native checks cover Preferences size rows/footer, connection controls, complete deck labels, valid/invalid damage-spinner input and Choose, visible filter navigation and full Cancel restoration, all four tooltip corners/bottom rows, card appearance and the corrected picked-card toolbar. A draft divider was dragged at 125%; sorting/Piles controls were operated after the toolbar fix. A plain-choice submission and 150%-to-100% portrait movement/return also passed.

The toolbar regression first failed its complete-height assertion at font 34, then passed at fonts 14/17/23/34 and a 300px viewport. Narrow-toolbar scrolling has automated coverage; a successful narrow native resize is not claimed from the fractional session. Original display settings were restored afterward.

Earlier native 100%/200% checks include a private loopback game's Mulligan/Keep, shortcuts, mana payment/Undo, targeting/Cancel, stack resolution and damage/death updates. A full live game was not repeated at each fractional scale. A separate production-callback draft completed 45 picks with seven bots; this is not 45 physical picks. Native evidence uses the combined build, while the isolated branch table establishes per-contribution regression results.

No native macOS host or formal native whole-game FPS/retained-heap baseline is available. Warm offscreen six-card/mana sheet samples are separate measurements:

| Transform | Before ms | After ms | Six-card raster bytes after |
| ---: | ---: | ---: | ---: |
| 1.00 | 2.724 | 2.491 | 1,101,600 |
| 1.25 | 4.856 | 5.259 | 1,722,600 |
| 1.50 | 6.383 | 7.092 | 2,481,840 |
| 2.00 | 12.470 | 8.444 | 4,406,400 |

The before six-card raster allocation was 1,101,600 bytes at every transform. These samples exclude other caches and retained heap, and are not a whole-game performance guarantee. The prepared asset pack is limited; absent symbols retain existing fallback behavior.