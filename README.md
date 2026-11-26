# Oisin

Personal Pixel 6 + Pixel Watch 3 app for quiet time awareness through vibration-only nudges.

## Versioning

Oisin follows [Semantic Versioning](https://semver.org/). Commit messages use [Conventional Commits](https://www.conventionalcommits.org/), and release tags use the `vMAJOR.MINOR.PATCH` format.

## Open and run

Open this directory in Android Studio with an Android SDK and JDK installed. Build and install both `mobile` and `wear`. The mobile and Wear modules intentionally share the same application ID so they can be installed as the phone/watch pair.

On the phone, choose the days and active hours, then tap **Save to watch**. On the watch, grant notification permission if prompted. The watch stores the schedule locally and provides **Pause**/**Resume** controls.

The watch uses exact alarms when the user grants the system's “Alarms & reminders” access. Until then it falls back to an allow-while-idle alarm, which may be less precise.
