# XMage scaling review evidence

Selected screenshots and local test results for six focused GUI contributions to `magefree/mage`, recorded on 2026-10-14/15. This orphan evidence branch is separate from every proposed code branch: screenshots do not enter the upstream code diffs.

See [test-results.md](test-results.md) for exact source hashes, isolated suites, runtime matrix, reproduction commands and coverage limits. [screenshot-manifest.json](screenshot-manifest.json) records each image's SHA256 and dimensions. Images are byte-identical to their reviewed captures; extensions match their actual encodings.

## Screenshot captions

- Connection and damage font-34 before/after pairs are matched background component captures on a 2732x1536 canvas. They demonstrate layout, not native input.
- The 2x render pair uses the same six-card/mana sheet at a synthetic 2x transform on a 2320x1240 canvas. MTGO rendering is above image rendering.
- Native draft, filter and popup captures show actual pointer/keyboard states. Draft retains fifteen cards after divider adjustment; the filter shows ga/Gamma; popups show the lower-right case on each actual display.
- The native portrait deck pair demonstrates NEW/SAVE before/after layout invalidation. Window bounds differ, so it is not a pixel-matched geometry comparison.
- The actual-125% picked-toolbar pair demonstrates the fixed height cap before/after correction. Card order differs in the private fixture; compare toolbar geometry. The 150% capture confirms the row after a live scale change.

All selected captures show private test fixtures. Native integration screenshots were taken with the combined build; branch-specific automated results are reported separately.