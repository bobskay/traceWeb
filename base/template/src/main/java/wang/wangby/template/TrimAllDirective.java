package wang.wangby.template;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/** 删除多余空格 */
public class TrimAllDirective extends Directive {

	@Override
	public String getName() {
		return "trimAll";
	}

	@Override
	public int getType() {
		return BLOCK;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
			ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		StringWriter wr = new StringWriter();
		node.jjtGetChild(0).render(context, wr);
		String text = wr.toString();
		writer.write(text.replaceAll("\\s+", " ").trim());
		return true;
	}
}
