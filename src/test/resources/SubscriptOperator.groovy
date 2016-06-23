def someObject = [:]
someObject[0]
someObject['hey','ho']
someObject[0..10]
someObject[0..<11]

someObject[2,3] = '1'
someObject[[2,3]] = '2'
path.someObject[[2,3]] = '3'

someObject[some(0)]
someObject[]
someObject[0..10] = 'fish'
someObject[0..<11] = 'meh'

someObject[some(0), 42] = 127

// Remember the special case for the spread operator
someObject[*list]
someObject[1, *list, 3]

// Also take care of spread-map:
def someMap= [c: 3, d: 4]
def mymap = [a: 1, b: 2, *:someMap, e: 66]
someFunc(firstArg: 1, otherArg: 2)
