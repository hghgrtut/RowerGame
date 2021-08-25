package by.profs.rowgame.view.activity

interface InfoBar {
    fun showAll() {
        showDay()
        showFame()
        showMoney()
    }

    fun showDay()

    fun showFame()

    fun showMoney()

    fun nextAndShowDay()

    fun changeFame(amount: Int)

    fun changeMoney(amount: Int)

    fun getDay(): Int

    fun getFame(): Int

    fun getMoney(): Int
}