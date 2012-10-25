package jabara.jpa_guice;

import jabara.general.ReflectionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

class CloseBarrierEntityManagerHandler implements InvocationHandler {

    private final EntityManager original;
    private final Runnable      operationAtClose;

    public CloseBarrierEntityManagerHandler(final EntityManager pOriginal, final Runnable pOperationAtClose) {
        this.original = pOriginal;
        this.operationAtClose = pOperationAtClose;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(@SuppressWarnings("unused") final Object pProxy, final Method pMethod, final Object[] pArguments) {
        if ("close".equals(pMethod.getName())) { //$NON-NLS-1$
            // closeは原則禁止.
            return null;
        }
        return ReflectionUtil.invoke(this.original, pMethod, pArguments);
    }

    void closeReality() {
        this.original.close();
        this.operationAtClose.run();
    }
}