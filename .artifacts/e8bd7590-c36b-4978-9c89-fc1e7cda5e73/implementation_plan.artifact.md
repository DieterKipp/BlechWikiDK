# Implementation Plan - Icons im Dropdown (Overflow Menu) anzeigen

Standardmäßig zeigt Android im Overflow-Menü (dem Dropdown, wenn man auf die drei Punkte klickt) keine Icons an, selbst wenn diese im XML definiert sind. Um die Icons dennoch anzuzeigen, muss eine interne Methode des `MenuBuilder` via Reflection aufgerufen werden.

## User Review Required

> [!IMPORTANT]
> Diese Lösung verwendet Reflection, um auf eine interne (restricted) Methode von Android zuzugreifen. Dies ist der gängige Weg für `AppCompat`, da es keine öffentliche API dafür gibt.

## Proposed Changes

### [MainActivity](file:///C:/Daten/Android/BlechWikiDK/app/src/main/java/com/example/blechwikidk/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Daten/Android/BlechWikiDK/app/src/main/java/com/example/blechwikidk/MainActivity.kt)
Wir überschreiben die Methode `onMenuOpened`, um die Sichtbarkeit der optionalen Icons im Menü zu erzwingen.

```kotlin
override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
    if (menu.javaClass.simpleName == "MenuBuilder") {
        try {
            val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return super.onMenuOpened(featureId, menu)
}
```

## Verification Plan

### Manual Verification
- App starten.
- Das Overflow-Menü (drei Punkte) öffnen.
- Überprüfen, ob die Icons neben den Texten (z.B. bei "update Tabelle vom Server") angezeigt werden.
