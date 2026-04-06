import SwiftUI
import shared

@main
struct iosAppApp: App {
    init() {
        #if DEBUG
        let isDebug = true
        #else
        let isDebug = false
        #endif
        MainViewControllerKt.doInitKoin(baseUrl: "http://localhost:8000", isDebug: isDebug)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
