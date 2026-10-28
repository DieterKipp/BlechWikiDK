# Walkthrough - Icons im Dropdown (Overflow Menu) anzeigen

Ich habe die Funktionalität hinzugefügt, damit Icons im Overflow-Menü (Dropdown) angezeigt werden.

## Änderungen

### [MainActivity](file:///C:/Daten/Android/BlechWikiDK/app/src/main/java/com/example/blechwikidk/MainActivity.kt)
Die Methode `onMenuOpened` wurde überschrieben. Sie nutzt Reflection, um auf die interne Methode `setOptionalIconsVisible(true)` des `MenuBuilder` zuzugreifen. Dies erzwingt die Anzeige von Icons neben den Menüeinträgen im Dropdown-Menü.

```kotlin
override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
    if (menu.javaClass.simpleName == "MenuBuilder") {
        try {
            val method = menu.javaClass.getDeclaredMethod(
                "setOptionalIconsVisible",
                Boolean::class.javaPrimitiveType
            )
            method.isAccessible = true
            method.invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return super.onMenuOpened(featureId, menu)
}
```

## Verifizierung
- **Build**: Erfolgreich abgeschlossen.
- **Funktion**: Wenn Sie nun das Overflow-Menü (die drei Punkte) in der App öffnen, sollten die im XML definierten Icons sichtbar sein.
