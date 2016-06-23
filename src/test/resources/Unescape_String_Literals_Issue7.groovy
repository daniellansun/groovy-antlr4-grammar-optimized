'hello\tworld\r\nYours \u5b59\u5c9a. ${just a string literal} \002\000\001\006'
'''
hello\tworld
\u5b59\u5c9a
\002\000\001\006
${just a string literal}
'''
"hello\tworld"
"hello\t${name}!\r\ndate:${date};location:\t${location}\n; Yours \u5b59\u5c9a.\002\000\001\006"
"""
hello\t${name}!
\t\t\t${date}\r\n
${location}.
Yours \u5b59\u5c9a.
\002\000\001\006
"""

def a = "\u003b \\u0026lt\\u003b1\\u0026gt\\u003bHello\\u003b \\\u003b \\\\u003b \002 \\002\\000\\001\\006 \\\002\\\000 \\\\002   \n \\n \\\n\\\\n"

a = /\u003b \\u003b \\\u003b \\\\u003b \002 \\002 \\\002 \\\\002 \n \\n \\\n \\\\n/
a = $/\u003b \\u003b \\\u003b \\\\u003b \002 \\002 \\\002 \\\\002 \n \\n \\\n \\\\n/$
