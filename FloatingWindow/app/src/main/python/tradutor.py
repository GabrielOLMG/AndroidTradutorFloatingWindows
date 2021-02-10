from translate import Translator
def trad():
    translator= Translator(to_lang="pt")
    translation = translator.translate("This is a pen.")
    return translation
