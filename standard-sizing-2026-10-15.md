# GUI refresh safety and original sizing

## Download-completion layout crash correction

The downloader finishes on a background thread and calls GUISizeHelper.refreshGUIAndCards(false). Refreshing live Swing UI delegates there can overlap EDT painting and layout: the reported log contains null scrollbar buttons, combo-box arrows and BoxLayout size requirements. The shared refresh entry now queues the entire operation on the EDT when called by a worker. Calls already on the EDT still run directly; the worker does not block waiting for Swing.

Two deterministic regressions fail on the previous code because it replaces live UI delegates while the EDT is busy. Both pass with the fix: normal image refresh and theme refresh are queued, all delegate updates run on the EDT, and the internal dialog can be dragged, laid out and painted afterward. The production fix is five lines and retains release 8 and all existing dependencies/forms.

The local installation's eight GUI size preferences were reset to the application baseline: 14 for dialog/menu, chat, cards and player panels; 17 for popup hints. This is a local user-requested settings reset, not a change to application defaults or other users' preferences. Windows display settings and all other preferences are unchanged. No backup was created.

The dialog-font preference previously affected selected application widgets, while standard Swing controls retained look-and-feel or form fonts. Preserve those existing bindings for every font setting. Native Java2D display transforms provide DPI scaling; the layout helper does not replace fonts or apply an additional DPI font multiplier.

The correction also retains the original deck-editor sidebar command dimensions and selector layout. A horizontal viewport permits flexible layouts to fill the available width, with scrolling below their minimum. Empty non-UI-resource borders prevent Nimbus from adding six pixels of padding during style changes. Original compact size hints are retained; explicitly enlarged fonts may expand their size hints.

Baseline client source: `492369c7844b8d76479f0393ff9eb6b0b0cf30d7`. Combined: `1857e0b8fb1ba6f4e13793f1f3e441f489123fde`. Foundation: `845b80e763899c7234f755a0f35d06cf0da7be96`.

## Matched original/installed comparisons

Every pair uses the same Java 17 runtime, look and feel, preference values, canvas and pane settings. The original client was freshly built from unchanged upstream source; the corrected captures load the actual client jar installed in C:.

- Connection dialog and picked-card toolbar: all pixels match at settings 14, 17, 23 and 34.
- Production main toolbar and Games played label: all pixels match at settings 14, 17, 23 and 34 on the actual 100%-scale graphics device. The toolbar preserves the original font+12 row height and font+6 control heights; its full content width enables horizontal scrolling when needed.
- The same four settings also match using a hidden native peer on the actual Windows 100%-scale graphics device, without overriding Java's scale.
- Native Windows 200% device: the connection dialog and picked-card toolbar match upstream at settings 14 and 34, without overriding Java's scale. Setting 14 shows the restored application baseline with Windows supplying the 2x display transform.
- Full deck-editor form: all pixels match at settings 14 and 34 on a 1920x1080 canvas. Both sides use divider location 678 to hold pane state constant; this tests control sizing rather than automatic initial splitter allocation.
- Simulated 125%, 150% and 200%: all pixels match at settings 14 and 34.
- The regression suite additionally checks standard-control fonts and preferred sizes for every selectable dialog-font value, 10 through 50. Existing manual font preferences remain effective for the widgets that used them before.
- Main-toolbar height and narrow-viewport scrolling regressions cover every dialog-font value, 10 through 50. The main-toolbar capture initializes the production form and sizing method while skipping client startup, database access and session threads.

These are background component renders attached to hidden native peers for device font measurements, not desktop input screenshots. No Windows display setting was changed. Full-editor fixtures use empty production forms with matching pane positions, not a running game or populated deck.

| Pair | Scale | Dialog setting | Differing pixels |
| --- | ---: | ---: | ---: |
| native100-font14 | 1.0 | 14 | 0 |
| native100-font17 | 1.0 | 17 | 0 |
| native100-font23 | 1.0 | 23 | 0 |
| native100-font34 | 1.0 | 34 | 0 |
| 100-font14 | 1.0 | 14 | 0 |
| 100-font17 | 1.0 | 17 | 0 |
| 100-font23 | 1.0 | 23 | 0 |
| 100-font34 | 1.0 | 34 | 0 |
| scale1.25-font14 | 1.25 | 14 | 0 |
| scale1.25-font34 | 1.25 | 34 | 0 |
| scale1.5-font14 | 1.5 | 14 | 0 |
| scale1.5-font34 | 1.5 | 34 | 0 |
| scale2-font14 | 2.0 | 14 | 0 |
| scale2-font34 | 2.0 | 34 | 0 |
| deck-100-font14 | 1.0 | 14 | 0 |
| deck-100-font34 | 1.0 | 34 | 0 |
| menu-native100-font14 | 1.0 | 14 | 0 |
| menu-native100-font17 | 1.0 | 17 | 0 |
| menu-native100-font23 | 1.0 | 23 | 0 |
| menu-native100-font34 | 1.0 | 34 | 0 |
| native200-font14 | 2.0 | 14 | 0 |
| native200-font34 | 2.0 | 34 | 0 |

## Tests and compatibility

- Actual installed jar, bundled Java 17: all 46 GUI tests pass at each simulated scale 1.0, 1.25, 1.5 and 2.0.
- Combined Windows Java 17/21: 71 cases, 61 passed, 10 pre-existing skips; zero failures/errors.
- Combined Java 8 with bundled JavaFX: 71 cases, 60 passed, 11 pre-existing skips; zero failures/errors.
- All 15 edited forms regenerate in NetBeans 8.2. Regenerated Java compiles for release 8 and passes 46 GUI tests.
- Existing dependencies, user preference values, and client/server compatibility are retained.
- Saved divider positions remain respected. The controlled deck comparison above does not claim identical automatic initial pane allocation.
- Earlier physical 125%/150% interaction evidence is historical; it is not relabeled as validation of this revision.

| Isolated clean Java 21 branch | Cases | Passed | Skipped |
| --- | ---: | ---: | ---: |
| codex/gui-font-layout | 52 | 42 | 10 |
| codex/gui-draft-divider | 56 | 46 | 10 |
| codex/gui-damage-layout | 55 | 45 | 10 |
| codex/gui-popup-position | 54 | 44 | 10 |
| codex/gui-hidpi-rendering | 58 | 48 | 10 |
| codex/gui-deck-filter | 56 | 46 | 10 |

All branch runs have zero failures/errors.

```text
mvn -B -ntp -pl Mage.Client test "-Dtest=GuiLayoutUtilTest,DialogLayoutTest" "-Djava.util.prefs.PreferencesFactory=mage.client.util.gui.GuiTestPreferencesFactory"
```

## Screenshots

Actual 100%-device peer, font setting 34. Original:

![Original 100%, font setting 34](screenshots/standard-sizing/original-native100-font34.png)

Installed correction, same settings:

![Installed 100%, font setting 34](screenshots/standard-sizing/restored-native100-font34.png)

Deck-editor form, 100%, setting 34, matching divider state. Original:

![Original deck editor at 100%](screenshots/standard-sizing/original-deck-100-font34.png)

Installed correction:

![Installed deck editor at 100%](screenshots/standard-sizing/restored-deck-100-font34.png)

Production main toolbar and Games played label, native 100%, setting 34. Original:

![Original main toolbar](screenshots/standard-sizing/original-menu-native100-font34.png)

Installed correction, same settings:

![Installed main toolbar](screenshots/standard-sizing/restored-menu-native100-font34.png)

Installed at simulated 200%, setting 34:

![Installed at 200%](screenshots/standard-sizing/restored-scale2-font34.png)

Installed baseline font setting 14 on the actual Windows 200% device:

![Installed baseline on the 200% device](screenshots/standard-sizing/restored-native200-font14.png)

All pairs and sanitized component measurements are committed alongside this report. See standard-sizing-comparisons.json and standard-sizing-screenshot-manifest.json for exact dimensions, results and hashes.
