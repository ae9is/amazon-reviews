package amazonrev.util.exception;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable err, DataFetchingEnvironment env) {
    if (err instanceof BadRequestException) {
      return GraphqlErrorBuilder
          .newError()
          .errorType(ErrorType.BAD_REQUEST)
          .message(err.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else {
      return GraphqlErrorBuilder
          .newError()
          .errorType(ErrorType.INTERNAL_ERROR)
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    }
  }
}
