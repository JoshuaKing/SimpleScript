# SimpleScript
Java-Like language which compiles to javascript

## Primitives

Simple-Script, has 7 primitives:

| Primitive | Keyword | Example Value |
| --------- | ------- | ------------- |
| Boolean   | boolean | true/false    |
| Integer   | int     | 0/2/10/42     |
| Float     | float   | 0.5/3.1419    |
| Enumerator| enum    | { Male, Female } |
| String    | string  | "Hello, World" |
| Map       | map     | { key: value } |
| List      | list    | [ 1, 2, 4, 2 ] |

Strings aren't just null terminates character arrays, they are complete objects so you can convert them to uppercase (`str.toUpperCase()`)

Lists are the same as arrays in other languages, except they have all the attributes you'd expect of a normal class object.  All elements must be of the same (automatically detected) type.

Think of Maps (a.k.a associative arrays) as you would JSON: each element in a map does not have to be the same type, and you may access them without type safety, so you can do this:
```
map myMap = {
    Name: "John",
    Age: 32,
    // future value here
    Pets: {
        Yuki: "Dog"
    },
    TV: [
        "Game Of Thrones",
        "Futurama",
        "Modern Family"
    ]
};

myMap.Gender = "M";     // Non-existent key - assigned new value
```