package app.kiddos.codeeditor.syntax

import app.kiddos.codeeditor.data.ProgrammingLanguage

object LanguageRules {
    private val cppKeywords = setOf(
        "alignas", "alignof", "and", "and_eq", "asm", "auto", "bitand", "bitor",
        "bool", "break", "case", "catch", "char", "char8_t", "char16_t", "char32_t",
        "class", "compl", "concept", "const", "consteval", "constexpr", "constinit",
        "const_cast", "continue", "co_await", "co_return", "co_yield", "decltype",
        "default", "delete", "do", "double", "dynamic_cast", "else", "enum",
        "explicit", "export", "extern", "false", "float", "for", "friend", "goto",
        "if", "inline", "int", "long", "mutable", "namespace", "new", "noexcept",
        "not", "not_eq", "nullptr", "operator", "or", "or_eq", "override", "private",
        "protected", "public", "register", "reinterpret_cast", "requires", "return",
        "short", "signed", "sizeof", "static", "static_assert", "static_cast",
        "struct", "switch", "template", "this", "thread_local", "throw", "true",
        "try", "typedef", "typeid", "typename", "union", "unsigned", "using",
        "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq"
    )

    private val cppTypes = setOf(
        "string", "wstring", "u8string", "u16string", "u32string", "string_view",
        "vector", "map", "unordered_map", "set", "unordered_set", "pair", "tuple",
        "unique_ptr", "shared_ptr", "weak_ptr", "size_t", "int8_t", "int16_t",
        "int32_t", "int64_t", "uint8_t", "uint16_t", "uint32_t", "uint64_t",
        "intptr_t", "uintptr_t", "ptrdiff_t", "iostream", "istream", "ostream",
        "cin", "cout", "cerr", "endl"
    )

    private val pythonKeywords = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue",
        "def", "del", "elif", "else", "except", "finally", "for", "from",
        "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
        "or", "pass", "raise", "return", "try", "while", "with", "yield",
        "True", "False", "None", "self", "cls"
    )

    private val pythonTypes = setOf(
        "int", "float", "complex", "str", "bytes", "bytearray", "list", "tuple",
        "set", "frozenset", "dict", "bool", "range", "object", "type", "Any",
        "Union", "Optional", "List", "Dict", "Set", "Tuple", "Callable"
    )

    private val rustKeywords = setOf(
        "as", "async", "await", "break", "const", "continue", "crate", "dyn",
        "else", "enum", "extern", "false", "fn", "for", "if", "impl", "in",
        "let", "loop", "match", "mod", "move", "mut", "pub", "ref", "return",
        "self", "Self", "static", "struct", "super", "trait", "true", "type",
        "unsafe", "use", "where", "while"
    )

    private val rustTypes = setOf(
        "i8", "i16", "i32", "i64", "i128", "isize",
        "u8", "u16", "u32", "u64", "u128", "usize",
        "f32", "f64", "bool", "char", "str", "String",
        "Option", "Some", "None", "Result", "Ok", "Err",
        "Vec", "Box", "Rc", "Arc", "HashMap", "HashSet", "BTreeMap"
    )

    private val javaKeywords = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double",
        "else", "enum", "extends", "final", "finally", "float", "for",
        "goto", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "try",
        "void", "volatile", "while", "true", "false", "null", "var", "record",
        "sealed", "permits", "yield"
    )

    private val javaTypes = setOf(
        "String", "Object", "Class", "System", "Integer", "Long", "Double",
        "Float", "Boolean", "Character", "Byte", "Short", "List", "ArrayList",
        "LinkedList", "Map", "HashMap", "TreeMap", "Set", "HashSet", "TreeSet",
        "Optional", "Stream", "Collectors", "Thread", "Runnable", "Exception",
        "Throwable", "RuntimeException", "StringBuilder", "StringBuffer"
    )

    private val kotlinKeywords = setOf(
        "as", "as?", "break", "class", "continue", "do", "else", "false", "for",
        "fun", "if", "in", "!in", "interface", "is", "!is", "null", "object",
        "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "val", "var", "when", "while", "by", "companion", "constructor", "init",
        "data", "enum", "inner", "open", "override", "private", "protected",
        "public", "sealed", "suspend", "tailrec", "vararg"
    )

    private val jsTsKeywords = setOf(
        "break", "case", "catch", "class", "const", "continue", "debugger",
        "default", "delete", "do", "else", "export", "extends", "finally",
        "for", "function", "if", "import", "in", "instanceof", "new", "return",
        "super", "switch", "this", "throw", "try", "typeof", "var", "void",
        "while", "with", "yield", "let", "static", "enum", "await", "async",
        "true", "false", "null", "undefined", "interface", "type", "implements",
        "package", "private", "protected", "public", "any", "never", "unknown"
    )

    private val sqlKeywords = setOf(
        "select", "from", "where", "insert", "into", "values", "update", "set",
        "delete", "create", "table", "drop", "alter", "add", "join", "inner",
        "left", "right", "full", "outer", "on", "group", "by", "order", "asc",
        "desc", "having", "limit", "distinct", "union", "all", "as", "is",
        "not", "null", "in", "between", "like", "and", "or", "case", "when",
        "then", "else", "end", "primary", "key", "foreign", "references"
    )

    fun getKeywords(language: ProgrammingLanguage): Set<String> {
        return when (language) {
            ProgrammingLanguage.CPP -> cppKeywords
            ProgrammingLanguage.PYTHON -> pythonKeywords
            ProgrammingLanguage.RUST -> rustKeywords
            ProgrammingLanguage.JAVA -> javaKeywords
            ProgrammingLanguage.KOTLIN -> kotlinKeywords
            ProgrammingLanguage.JAVASCRIPT,
            ProgrammingLanguage.TYPESCRIPT -> jsTsKeywords
            ProgrammingLanguage.SQL -> sqlKeywords
            else -> emptySet()
        }
    }

    fun getTypes(language: ProgrammingLanguage): Set<String> {
        return when (language) {
            ProgrammingLanguage.CPP -> cppTypes
            ProgrammingLanguage.PYTHON -> pythonTypes
            ProgrammingLanguage.RUST -> rustTypes
            ProgrammingLanguage.JAVA -> javaTypes
            ProgrammingLanguage.KOTLIN -> javaTypes
            else -> emptySet()
        }
    }
}
