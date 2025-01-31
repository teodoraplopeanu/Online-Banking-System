package org.poo.main.commands;

import org.poo.fileio.CommandInput;
import org.poo.main.commands.bankingData.account.*;
import org.poo.main.commands.bankingData.card.CheckCardStatus;
import org.poo.main.commands.bankingData.card.CreateCard;
import org.poo.main.commands.bankingData.card.DeleteCard;
import org.poo.main.commands.moneyTransfer.*;
import org.poo.main.commands.reports.*;

public final class CommandFactory {

    /**
     * Returns the command that has to be executed
     * */

    private CommandFactory() { }

    /**
     * Returns the corresponding command class to the input given
     * */
    public static Command createCommand(final CommandInput input) {
        return switch (input.getCommand()) {
            case "printUsers" -> new PrintUsers();
            case "printTransactions" -> new PrintTransactions();
            case "addAccount" -> new AddAccount();
            case "deleteAccount" -> new DeleteAccount();
            case "createCard", "createOneTimeCard" -> new CreateCard();
            case "deleteCard" -> new DeleteCard();
            case "addFunds" -> new AddFunds();
            case "payOnline" -> new PayOnline();
            case "sendMoney" -> new SendMoney();
            case "setAlias" -> new SetAlias();
            case "setMinimumBalance" -> new SetMinBalance();
            case "checkCardStatus" -> new CheckCardStatus();
            case "splitPayment" -> new SplitPayment();
            case "report" -> new Report();
            case "spendingsReport" -> new SpendingReport();
            case "changeInterestRate" -> new ChangeInterestRate();
            case "addInterest" -> new AddInterest();
            case "withdrawSavings" -> new WithdrawSavings();
            case "upgradePlan" -> new UpgradePlan();
            case "cashWithdrawal" -> new CashWithdrawal();
            case "acceptSplitPayment" -> new AcceptSplitPayment();
            case "rejectSplitPayment" -> new RejectSplitPayment();
            case "changeSpendingLimit" -> new ChangeSpendingLimit();
            case "changeDepositLimit" -> new ChangeDepositLimit();
            case "addNewBusinessAssociate" -> new AddNewBusinessAssociate();
            case "businessReport" -> new BusinessReport();
            default -> null;
        };
    }
}
