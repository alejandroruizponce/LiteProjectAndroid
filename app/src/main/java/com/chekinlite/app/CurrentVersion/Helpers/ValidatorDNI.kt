package com.chekinlite.app.CurrentVersion.Helpers


internal class ValidatorDNI(private val dni: String) {


    fun validar(): Boolean {


        var letraMayuscula = "" //Guardaremos la letra introducida en formato mayúscula

        // Aquí excluimos cadenas distintas a 9 caracteres que debe tener un dni y también si el último caracter no es una letra
        if (dni.length != 9 || Character.isLetter(this.dni[8]) == false) {
            return false
        }


        // Al superar la primera restricción, la letra la pasamos a mayúscula
        letraMayuscula = this.dni.substring(8).toUpperCase()

        // Por último validamos que sólo tengo 8 dígitos entre los 8 primeros caracteres y que la letra introducida es igual a la de la ecuación
        // Llamamos a los métodos privados de la clase soloNumeros() y letraDNI()
        return if (soloNumeros() == true && letraDNI() == letraMayuscula) {
            true
        } else {
            false
        }
    }

    private fun soloNumeros(): Boolean {

        var i: Int
        var j = 0
        var numero =
            "" // Es el número que se comprueba uno a uno por si hay alguna letra entre los 8 primeros dígitos
        var miDNI = "" // Guardamos en una cadena los números para después calcular la letra
        val unoNueve = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

        i = 0
        while (i < this.dni.length - 1) {
            numero = this.dni.substring(i, i + 1)

            j = 0
            while (j < unoNueve.size) {
                if (numero == unoNueve[j]) {
                    miDNI += unoNueve[j]
                }
                j++
            }
            i++
        }

        return if (miDNI.length != 8) {
            false
        } else {
            true
        }
    }

    private fun letraDNI(): String {
        // El método es privado porque lo voy a usar internamente en esta clase, no se necesita fuera de ella

        // pasar miNumero a integer
        val miDNI = Integer.parseInt(this.dni.substring(0, 8))
        var resto = 0
        var miLetra = ""
        val asignacionLetra = arrayOf(
            "T",
            "R",
            "W",
            "A",
            "G",
            "M",
            "Y",
            "F",
            "P",
            "D",
            "X",
            "B",
            "N",
            "J",
            "Z",
            "S",
            "Q",
            "V",
            "H",
            "L",
            "C",
            "K",
            "E"
        )

        resto = miDNI % 23

        miLetra = asignacionLetra[resto]

        return miLetra
    }
}