promise = promise.then { it * 2 } then { throw new RuntimeException("bad")} then { it + 6 }
promise = promise.then { it * 2 } then { throw new RuntimeException("bad")} then { it + 6 } someProperty
promise = promise.then { it * 2 } then { throw new RuntimeException("bad")} then { it + 6 } 'someProperty'
promise = promise.then { it * 2 } then { throw new RuntimeException("bad")} then { it + 6 } "someProperty"
promise = promise.then { it * 2 } then { throw new RuntimeException("bad")} then { it + 6 } "somePropert${'y'}"
result = [1, 2, 3].size().plus 1 plus 2