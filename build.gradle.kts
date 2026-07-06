/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    // This is the root build file — no plugins applied here
    // All plugins are applied via convention plugins in build-logic
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}