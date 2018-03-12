

class mylog implements Serializable {

    def info(message) {
        echo "INFO: ${message}"
    }

    def warning(message) {
        echo "WARNING: ${message}"
    }

}