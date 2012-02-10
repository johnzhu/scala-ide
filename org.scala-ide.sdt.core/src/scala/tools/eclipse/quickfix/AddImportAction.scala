package scala.tools.eclipse.quickfix

import org.eclipse.core.commands.{ AbstractHandler, ExecutionEvent }
import org.eclipse.jface.action.{ Action, AbstractAction, IAction }
import scala.tools.eclipse.refactoring.ActionAdapter

/**
 * Dummy noop action for now to suppress errors (see ticket #2961) -- but see ImportCompletionProposal
 */
class AddImportAction extends ActionAdapter {

  def run(action: IAction) {}
}

