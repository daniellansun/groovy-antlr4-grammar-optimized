extension == '*' ? '' : extension ?: ''
extension == '*' ? '' : extension == '/' ? '%' : '#'
extension ?: extension ?: '#'
extension == '*' ? '' : extension == '/' ? extension ?: '%' : '#'