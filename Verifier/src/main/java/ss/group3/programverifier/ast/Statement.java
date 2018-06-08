package ss.group3.programverifier.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Statement extends AstNode {

    private Set<Contract> contractsThatApplyToThisStatement;
    private Set<Contract> unmodifiableView;

    public Statement() {
    }

    public void addContracts(Iterator<? extends Contract> contractIterator) {
        while (contractIterator.hasNext()) {
            addContract(contractIterator.next());
        }
    }

    public void addContracts(Iterable<? extends Contract> contracts) {
        for (Contract contract : contracts) {
            addContract(contract);
        }
    }

    public void addContracts(Contract... contracts) {
        for (Contract contract : contracts) {
            addContract(contract);
        }
    }

    public void addContract(Contract contract) {
        if (contractsThatApplyToThisStatement == null) {
            contractsThatApplyToThisStatement = new HashSet<>();
            unmodifiableView = Collections.unmodifiableSet(contractsThatApplyToThisStatement);
        }

        contractsThatApplyToThisStatement.add(contract);
    }

    public Set<Contract> getContracts() {
        if (unmodifiableView == null) {
            unmodifiableView = Collections.emptySet();
        }

        return unmodifiableView;
    }
}
